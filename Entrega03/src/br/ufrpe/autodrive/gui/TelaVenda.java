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
    @FXML private TableColumn<Venda, LocalDateTime> colData; // Nova coluna adicionada

    private ObservableList<Venda> dadosTabelaVendas = FXCollections.observableArrayList();
    private IGerenciadorVenda control;

    // 🟢 VOLTOU AO NOME ORIGINAL: Mantém compatibilidade exata com a linha 63 do seu ScreenManager
    public void injetarGerenciador(IGerenciadorVenda gV) {
        this.control = gV;
        atualizarTabela(control.listarTodasVendas());
    }

    @FXML
    public void initialize() {
        // Vincula os atributos básicos às colunas existentes
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colVeiculo.setCellValueFactory(new PropertyValueFactory<>("veiculo"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
        
        // Mapeamento e formatação da nova coluna de data
        colData.setCellValueFactory(new PropertyValueFactory<>("dataVenda"));
        
        // Customiza a exibição para formatar LocalDateTime como (dd/MM/yyyy HH:mm)
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

        tabelaVendas.setItems(dadosTabelaVendas);
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
                limparCamposFormulario();
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
        LocalDate inicio = dpFiltroInicio.getValue();
        LocalDate fim = dpFiltroFim.getValue();

        if (inicio == null || fim == null) {
            lblStatus.setText("Selecione ambas as datas para aplicar o filtro por período.");
            lblStatus.setStyle("-fx-text-fill: orange;");
            return;
        }

        if (inicio.isAfter(fim)) {
            lblStatus.setText("A data de início não pode ser posterior à data de término.");
            lblStatus.setStyle("-fx-text-fill: red;");
            return;
        }

        List<Venda> todas = control.listarTodasVendas();
        List<Venda> filtradas = new ArrayList<>();

        for (Venda v : todas) {
            if (v.getDataVenda() != null) {
                LocalDate dataV = v.getDataVenda().toLocalDate();
                if ((dataV.isEqual(inicio) || dataV.isAfter(inicio)) && 
                    (dataV.isEqual(fim) || dataV.isBefore(fim))) {
                    filtradas.add(v);
                }
            }
        }

        atualizarTabela(filtradas);
        lblStatus.setText("Filtro aplicado! Exibindo período selecionado.");
        lblStatus.setStyle("-fx-text-fill: blue;");
    }

    @FXML
    public void acaoLimparFiltro() {
        dpFiltroInicio.setValue(null);
        dpFiltroFim.setValue(null);
        if (control != null) {
            atualizarTabela(control.listarTodasVendas());
        }
        lblStatus.setText("Filtros de período limpos.");
        lblStatus.setStyle("-fx-text-fill: black;");
    }

    private void atualizarTabela(List<Venda> lista) {
        dadosTabelaVendas.clear();
        if (lista != null) {
            dadosTabelaVendas.addAll(lista);
        }
        tabelaVendas.refresh();
    }

    @FXML
    public void acaoAbrirFormulario() {
        painelMenuVendas.setVisible(false);
        painelMenuVendas.setManaged(false);
        painelFormulario.setVisible(true);
        painelFormulario.setManaged(true);
        lblStatus.setText("");

        if (control != null) {
            comboCliente.setItems(FXCollections.observableArrayList(control.listarTodosClientes()));
            comboVeiculo.setItems(FXCollections.observableArrayList(control.listarTodosVeiculos()));
            comboVendedor.setItems(FXCollections.observableArrayList(control.listarTodosVendedores()));
        }
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
            txtAreaAlertas.setText("Nenhum veículo precisa de revisão no momento.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("--- ALERTAS DE REVISÃO ---\n\n");
            for (Notificacao n : alertas) {
                sb.append("[!] ").append(n.getCliente().getNome()).append(" | ").append(n.getVeiculo().getModelo()).append("\n");
            }
            txtAreaAlertas.setText(sb.toString());
        }
    }
    
    @FXML
    public void acaoSairParaMenuPrincipal() {
        ScreenManager.getInstance().showMenuPrincipal();
    }
    
    private void limparCamposFormulario() {
        if (comboCliente != null) {
            comboCliente.setValue(null);
            comboCliente.setPromptText("");
            comboCliente.setPromptText("Selecione o Cliente...");
        }
        if (comboVeiculo != null) {
            comboVeiculo.setValue(null);
            comboVeiculo.setPromptText("");
            comboVeiculo.setPromptText("Selecione o Veículo...");
        }
        if (comboVendedor != null) {
            comboVendedor.setValue(null);
            comboVendedor.setPromptText("");
            comboVendedor.setPromptText("Selecione o Vendedor...");
        }
        if (txtEntrada != null) {
            txtEntrada.clear();
        }
    }
}
