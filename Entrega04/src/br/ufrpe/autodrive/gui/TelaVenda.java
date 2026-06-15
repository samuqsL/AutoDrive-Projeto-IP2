package br.ufrpe.autodrive.gui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import br.ufrpe.autodrive.negocio.IGerenciadorVenda;
import br.ufrpe.autodrive.negocio.beans.Cliente;
import br.ufrpe.autodrive.negocio.beans.Notificacao;
import br.ufrpe.autodrive.negocio.beans.Veiculo;
import br.ufrpe.autodrive.negocio.beans.Venda;
import br.ufrpe.autodrive.negocio.beans.Vendedor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class TelaVenda {

    @FXML private VBox painelMenuVendas;
    @FXML private VBox painelFormulario;

    @FXML private ComboBox<Cliente> comboCliente;
    @FXML private ComboBox<Veiculo> comboVeiculo;
    @FXML private ComboBox<Vendedor> comboVendedor;
    @FXML private TextField txtEntrada;
    @FXML private Label lblStatus;
    @FXML private TextArea txtAreaAlertas;

    // --- Componentes do Histórico de Vendas ---
    @FXML private DatePicker dpFiltroInicio;
    @FXML private DatePicker dpFiltroFim;
    @FXML private TableView<Venda> tabelaVendas;
    @FXML private TableColumn<Venda, Integer> colNumero;
    @FXML private TableColumn<Venda, Cliente> colCliente;
    @FXML private TableColumn<Venda, Veiculo> colVeiculo;
    @FXML private TableColumn<Venda, Double> colTotal;
    @FXML private TableColumn<Venda, LocalDateTime> colData;

    private ObservableList<Venda> dadosTabelaVendas = FXCollections.observableArrayList();
    private IGerenciadorVenda control;

    public void injetarGerenciador(IGerenciadorVenda gV) {
        this.control = gV;
        atualizarTabela(control.listarTodasVendas());
        // 🟢 ATUALIZAÇÃO PREVENTIVA: Garante que os dados entrem em cache assim que o ScreenManager focar a tela
        atualizarTodosComboBoxes();
    }

    @FXML
    public void initialize() {
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colVeiculo.setCellValueFactory(new PropertyValueFactory<>("veiculo"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
        
        colData.setCellValueFactory(new PropertyValueFactory<>("dataVenda"));
        
        colData.setCellFactory(column -> new TableCell<Venda, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(formatter));
                }
            }
        });

        // 🟢 TRAVA INTELIGENTE DE DATAS:
        dpFiltroInicio.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                dpFiltroFim.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        setDisable(empty || date.isBefore(newValue));
                    }
                });
                
                if (dpFiltroFim.getValue() != null && dpFiltroFim.getValue().isBefore(newValue)) {
                    dpFiltroFim.setValue(newValue);
                }
            }
        });

        tabelaVendas.setItems(dadosTabelaVendas);
    }

    // 🟢 NOVO MÉTODO CENTRALIZADO DE REFRESH: Executado de forma segura em JavaFX
    public void atualizarTodosComboBoxes() {
        if (control != null) {
            carregarComboVeiculosDisponiveis();
            
            List<Cliente> clientes = control.listarTodosClientes();
            if (clientes != null && comboCliente != null) {
                comboCliente.setItems(FXCollections.observableArrayList(clientes));
            }
            
            List<Vendedor> vendedores = control.listarTodosVendedores();
            if (vendedores != null && comboVendedor != null) {
                comboVendedor.setItems(FXCollections.observableArrayList(vendedores));
            }
        }
    }

    @FXML
    public void botaoConfirmarVenda() {
        Cliente c = comboCliente.getValue();
        Veiculo veic = comboVeiculo.getValue();
        Vendedor v = comboVendedor.getValue();
        String txt = txtEntrada.getText();

        if (c == null || veic == null || v == null || txt.isEmpty()) {
            lblStatus.setText("Por favor, preencha todos os campos do formulário.");
            lblStatus.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            double entrada = Double.parseDouble(txt);
            boolean sucesso = control.efetuarVenda(0, c.getCpf(), veic.getChassi(), v.getNome(), entrada, LocalDateTime.now());

            if (sucesso) {
                lblStatus.setText("Venda realizada e registrada com sucesso!");
                lblStatus.setStyle("-fx-text-fill: green;");
                
                // Limpa o formulário e força a atualização de quem sobrou em estoque
                limparCamposFormulario();
                atualizarTodosComboBoxes();
                atualizarTabela(control.listarTodasVendas());
            } else {
                lblStatus.setText("Falha: Verifique se o veículo está disponível ou se a entrada atingiu o mínimo de R$ 5.000,00.");
                lblStatus.setStyle("-fx-text-fill: red;");
            }
        } catch (NumberFormatException e) {
            lblStatus.setText("O campo de entrada aceita apenas valores numéricos válidos.");
            lblStatus.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void acaoFiltrarPorPeriodo() {
        LocalDate inicioSel = dpFiltroInicio.getValue();
        LocalDate fimSel = dpFiltroFim.getValue();

        if (inicioSel == null && fimSel == null) {
            atualizarTabela(control.listarTodasVendas());
            lblStatus.setText("Exibindo todas as vendas (sem filtro).");
            lblStatus.setStyle("-fx-text-fill: black;");
            return;
        }

        if (inicioSel == null || fimSel == null) {
            lblStatus.setText("Por favor, selecione ambas as datas (De / Até) para filtrar.");
            lblStatus.setStyle("-fx-text-fill: red;");
            return;
        }

        List<Venda> todas = control.listarTodasVendas();
        List<Venda> filtradas = new ArrayList<>();

        if (todas != null) {
            for (Venda v : todas) {
                if (v.getDataVenda() != null) {
                    LocalDate dataV = v.getDataVenda().toLocalDate();
                    boolean noIntervalo = (dataV.isAfter(inicioSel) || dataV.isEqual(inicioSel)) && 
                                          (dataV.isBefore(fimSel) || dataV.isEqual(fimSel));
                    if (noIntervalo) {
                        filtradas.add(v);
                    }
                }
            }
        }

        atualizarTabela(filtradas);
        
        if (filtradas.isEmpty()) {
            lblStatus.setText("Nenhuma venda encontrada no período selecionado.");
            lblStatus.setStyle("-fx-text-fill: orange;");
        } else {
            lblStatus.setText("Filtro aplicado! Exibindo " + filtradas.size() + " venda(s).");
            lblStatus.setStyle("-fx-text-fill: blue;");
        }
    }

    @FXML
    public void acaoLimparFiltro() {
        dpFiltroInicio.setValue(null);
        dpFiltroFim.setValue(null);
        dpFiltroFim.setDayCellFactory(null);
        
        if (control != null) {
            atualizarTabela(control.listarTodasVendas());
        }
        lblStatus.setText("Filtros de período limpos.");
        lblStatus.setStyle("-fx-text-fill: black;");
    }

    private void atualizarTabela(List<Venda> lista) {
        if (lista != null) {
            dadosTabelaVendas = FXCollections.observableArrayList(lista);
        } else {
            dadosTabelaVendas = FXCollections.observableArrayList();
        }
        tabelaVendas.setItems(dadosTabelaVendas);
    }
    
    private void carregarComboVeiculosDisponiveis() {
        if (comboVeiculo != null && control != null) {
            List<Veiculo> todos = control.listarTodosVeiculos();
            List<Veiculo> disponiveis = new ArrayList<>();
            
            if (todos != null) {
                for (Veiculo v : todos) {
                    if (v.getStatus() == br.ufrpe.autodrive.negocio.beans.StatusVeiculo.ESTOQUE || 
                        v.getStatus() == br.ufrpe.autodrive.negocio.beans.StatusVeiculo.DISPONIVEL) {
                        disponiveis.add(v);
                    }
                }
            }
            comboVeiculo.setItems(FXCollections.observableArrayList(disponiveis));
        }
    }

    @FXML
    public void acaoAbrirFormulario() {
        painelMenuVendas.setVisible(false);
        painelMenuVendas.setManaged(false);
        painelFormulario.setVisible(true);
        painelFormulario.setManaged(true);
        lblStatus.setText("");

        // Atualiza dinamicamente tudo antes de renderizar a troca de painel
        atualizarTodosComboBoxes();
    }

    @FXML
    public void acaoVoltarMenuVendas() {
        painelFormulario.setVisible(false);
        painelFormulario.setManaged(false);
        painelMenuVendas.setVisible(true);
        painelMenuVendas.setManaged(true);
        lblStatus.setText("");
        limparCamposFormulario();
    }

    @FXML
    public void botaoVerificarAlertas() {
        txtAreaAlertas.clear(); 
        List<Notificacao> alertas = control.listarAlertasRevisao(); 
        
        if (alertas == null || alertas.isEmpty()) {
            txtAreaAlertas.setText("✅ Nenhum veículo precisa de revisão no momento.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("=============================\n");
            sb.append(" ⚠️ ALERTAS DE REVISÃO ATIVOS \n");
            sb.append("=============================\n\n");
            
            for (Notificacao n : alertas) {
                sb.append("👤 CLIENTE: ").append(n.getCliente().getNome()).append("\n");
                sb.append("   └─ CPF: ").append(n.getCliente().getCpf()).append("\n");
                if (n.getCliente().getEmail() != null && !n.getCliente().getEmail().isBlank()) {
                    sb.append("   └─ E-mail: ").append(n.getCliente().getEmail()).append("\n");
                }
                
                sb.append("🚗 VEÍCULO: ").append(n.getVeiculo().getModelo()).append("\n");
                sb.append("   └─ Chassi: ").append(n.getVeiculo().getChassi()).append("\n");
                sb.append("   └─ Km Atual: ").append(n.getQuilometragem()).append(" km\n");
                sb.append("--------------------------------------------------\n\n");
            }
            txtAreaAlertas.setText(sb.toString());
        }
        
        // Dispara a exportação de PDF de Alertas coletados
        try {
            AlertasPdfService.exportarAlertasRevisao(alertas);
        } catch (Exception e) {
            System.err.println("Erro ao gerar PDF de alertas: " + e.getMessage());
        }
    }
    
    @FXML
    public void acaoSairParaMenuPrincipal() {
        ScreenManager.getInstance().showMenuPrincipal();
    }
    
    private void limparCamposFormulario() {
        if (comboCliente != null) {
            comboCliente.setValue(null);
            comboCliente.setPromptText("Selecione o Cliente...");
        }
        if (comboVeiculo != null) {
            comboVeiculo.setValue(null);
            comboVeiculo.setPromptText("Selecione o Veículo...");
        }
        if (comboVendedor != null) {
            comboVendedor.setValue(null);
            comboVendedor.setPromptText("Selecione o Vendedor...");
        }
        if (txtEntrada != null) {
            txtEntrada.clear();
        }
    }
}
