package br.ufrpe.autodrive.gui;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import br.ufrpe.autodrive.negocio.IGerenciadorVenda;
import br.ufrpe.autodrive.negocio.beans.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class TelaVenda {

    @FXML private VBox painelMenuVendas;
    @FXML private VBox painelFormulario;

    @FXML private ComboBox<Cliente> comboCliente;
    @FXML private ComboBox<Veiculo> comboVeiculo;
    @FXML private ComboBox<Vendedor> comboVendedor;
    @FXML private TextField txtEntrada;
    @FXML private DatePicker dpDataVenda;

    @FXML private Label lblStatus;
    @FXML private TextArea txtAreaAlertas; 

    @FXML private DatePicker dpFiltroInicio;
    @FXML private DatePicker dpFiltroFim;

    // CORREÇÃO DOS AVISOS: Explicitando os tipos genéricos das colunas para sumir o amarelo do Eclipse
    @FXML private TableView<Venda> tabelaVendas;
    @FXML private TableColumn<Venda, Integer> colNumero;
    @FXML private TableColumn<Venda, Cliente> colCliente;
    @FXML private TableColumn<Venda, Veiculo> colVeiculo;
    @FXML private TableColumn<Venda, Double> colTotal;

    private IGerenciadorVenda control; 

    public TelaVenda() {}

    @FXML
    public void initialize() {
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colVeiculo.setCellValueFactory(new PropertyValueFactory<>("veiculo"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
    }

    public void injetarGerenciador(IGerenciadorVenda gVenda) {
        this.control = gVenda;
        System.out.println("-> [TelaVenda] Gerenciador injetado com sucesso!");
        atualizarTabelaVendas(null); 
    }

    private void atualizarTabelaVendas(List<Venda> listaFiltrada) {
        if (control != null) {
            List<Venda> vendasExibir = listaFiltrada;
            
            if (vendasExibir == null) {
                // Como você adicionou o método no IGerenciador, chamamos ele direto de forma limpa!
                vendasExibir = control.listarTodasVendas();
            }
            
            if (vendasExibir != null) {
                ObservableList<Venda> obsList = FXCollections.observableArrayList(vendasExibir);
                tabelaVendas.setItems(obsList);
                tabelaVendas.refresh();
            }
        }
    }

    /**
     * CORREÇÃO COMPLETA DO BUG DE DATA:
     * Extrai apenas a parte da Data (LocalDate) removendo o fator de horas (Time) do cálculo.
     */
    @FXML
    public void acaoFiltrarPorPeriodo() {
        if (control == null || dpFiltroInicio.getValue() == null || dpFiltroFim.getValue() == null) {
            lblStatus.setText("⚠️ Selecione a data inicial e final para filtrar.");
            lblStatus.setStyle("-fx-text-fill: orange;");
            return;
        }

        LocalDate inicio = dpFiltroInicio.getValue();
        LocalDate fim = dpFiltroFim.getValue();

        // Evita que o usuário coloque uma data inicial maior que a final
        if (inicio.isAfter(fim)) {
            lblStatus.setText("❌ Erro: A data inicial não pode ser maior que a data final.");
            lblStatus.setStyle("-fx-text-fill: red;");
            return;
        }

        List<Venda> todas = control.listarTodasVendas();
        List<Venda> filtradas = new ArrayList<>();

        if (todas != null) {
            for (Venda v : todas) {
                if (v.getDataVenda() != null) {
                    // O SEGREDO: Converte o LocalDateTime da venda para LocalDate puro
                    LocalDate dataVendaPura = v.getDataVenda().toLocalDate();
                    
                    // Compara se o dia está estritamente dentro do intervalo (inclusive as pontas)
                    boolean igualOuDepois = dataVendaPura.isAfter(inicio) || dataVendaPura.isEqual(inicio);
                    boolean igualOuAntes = dataVendaPura.isBefore(fim) || dataVendaPura.isEqual(fim);

                    if (igualOuDepois && igualOuAntes) {
                        filtradas.add(v);
                    }
                }
            }
        }
        
        atualizarTabelaVendas(filtradas);
        lblStatus.setText("🔍 Filtro aplicado: " + filtradas.size() + " venda(s) encontrada(s).");
        lblStatus.setStyle("-fx-text-fill: blue;");
    }

    @FXML
    public void acaoLimparFiltro() {
        dpFiltroInicio.setValue(null);
        dpFiltroFim.setValue(null);
        lblStatus.setText("");
        atualizarTabelaVendas(null); 
    }

    @FXML
    public void acaoAbrirFormulario() {
        limparCamposFormulario();
        popularSeletoresCascata(); 
        
        // CORREÇÃO UX: Garante que o formulário abra sem nenhuma mensagem residual
        lblStatus.setText("");
        lblStatus.setStyle("");
        
        painelMenuVendas.setVisible(false);
        painelMenuVendas.setManaged(false);
        painelFormulario.setVisible(true);
        painelFormulario.setManaged(true);
    }

    private void popularSeletoresCascata() {
        if (control != null) {
            List<Cliente> listaC = control.listarTodosClientes();
            List<Veiculo> listaVe = control.listarTodosVeiculos();
            List<Vendedor> listaVend = control.listarTodosVendedores();

            if (listaC != null) comboCliente.setItems(FXCollections.observableArrayList(listaC));
            if (listaVe != null) comboVeiculo.setItems(FXCollections.observableArrayList(listaVe));
            if (listaVend != null) comboVendedor.setItems(FXCollections.observableArrayList(listaVend));
        }
    }

    @FXML
    public void acaoVoltarMenuVendas() {
        // CORREÇÃO UX: Limpa o status ao clicar em voltar
        lblStatus.setText("");
        lblStatus.setStyle("");
        
        painelFormulario.setVisible(false);
        painelFormulario.setManaged(false);
        painelMenuVendas.setVisible(true);
        painelMenuVendas.setManaged(true);
    }

    @FXML
    public void botaoConfirmarVenda() {
        lblStatus.setText(""); 
        try {
            Cliente c = comboCliente.getValue();
            Veiculo veic = comboVeiculo.getValue();
            Vendedor v = comboVendedor.getValue();

            if (c == null || veic == null || v == null || txtEntrada.getText().trim().isEmpty()) {
                lblStatus.setText("❌ ERRO: Selecione todos os campos obrigatórios.");
                lblStatus.setStyle("-fx-text-fill: red;");
                return;
            }

            double entrada = Double.parseDouble(txtEntrada.getText().trim());
            
            // CORREÇÃO DO BUG DE DATA: Define a data com o horário zerado (00:00:00)
            LocalDateTime dataVenda;
            if (dpDataVenda != null && dpDataVenda.getValue() != null) {
                // Converte o LocalDate do DatePicker para LocalDateTime às 00:00:00
                dataVenda = dpDataVenda.getValue().atStartOfDay(); 
            } else {
                // Se o usuário não escolheu data, assume o dia de hoje às 00:00:00
                dataVenda = LocalDate.now().atStartOfDay(); 
            }

            boolean sucesso = control.efetuarVenda(0, c.getCpf(), veic.getChassi(), v.getNome(), entrada, dataVenda);

            if (sucesso) {
                lblStatus.setText("✅ SUCESSO: Venda cadastrada!");
                lblStatus.setStyle("-fx-text-fill: green;");
                limparCamposFormulario();
                atualizarTabelaVendas(null); 
            } else {
                lblStatus.setText("❌ ERRO: Entrada mínima insuficiente, veículo indisponível, reservado ou sem RENAVAM.");
                lblStatus.setStyle("-fx-text-fill: red;");
            }
        } catch (NumberFormatException e) {
            lblStatus.setText("⚠️ ERRO: Insira uma entrada numérica válida.");
            lblStatus.setStyle("-fx-text-fill: orange;");
        }
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
        if (comboCliente != null) comboCliente.setValue(null);
        if (comboVeiculo != null) comboVeiculo.setValue(null);
        if (comboVendedor != null) comboVendedor.setValue(null);
        txtEntrada.clear();
        if (dpDataVenda != null) dpDataVenda.setValue(null);
        
        // CORREÇÃO UX: Centraliza a limpeza do status aqui também
        if (lblStatus != null) {
            lblStatus.setText("");
            lblStatus.setStyle("");
        }
    }
}
