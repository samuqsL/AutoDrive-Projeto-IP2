package br.ufrpe.autodrive.gui;

import java.time.LocalDate;
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
    // 🟢 REMOVIDO: dpDataVenda não é mais um atributo injetado da tela

    @FXML private Label lblStatus;
    @FXML private TextArea txtAreaAlertas; 

    @FXML private DatePicker dpFiltroInicio;
    @FXML private DatePicker dpFiltroFim;

    @FXML private TableView<Venda> tabelaVendas;
    @FXML private TableColumn<Venda, Integer> colNumero;
    @FXML private TableColumn<Venda, String> colCliente;
    @FXML private TableColumn<Venda, String> colVeiculo;
    @FXML private TableColumn<Venda, Double> colTotal;

    private IGerenciadorVenda control;
    private ObservableList<Venda> listaObservavelVendas;

    public void injetarGerenciador(IGerenciadorVenda control) {
        this.control = control;
        this.inicializarTabelaERelatorios();
    }

    private void inicializarTabelaERelatorios() {
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colVeiculo.setCellValueFactory(new PropertyValueFactory<>("veiculo"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));

        if (control != null) {
            comboCliente.setItems(FXCollections.observableArrayList(control.listarTodosClientes()));
            comboVeiculo.setItems(FXCollections.observableArrayList(control.listarTodosVeiculos()));
            comboVendedor.setItems(FXCollections.observableArrayList(control.listarTodosVendedores()));
            
            atualizarTabelaVendas(control.listarTodasVendas());
        }
    }

    private void atualizarTabelaVendas(List<Venda> listaVendas) {
        listaObservavelVendas = FXCollections.observableArrayList(listaVendas);
        tabelaVendas.setItems(listaObservavelVendas);
        tabelaVendas.refresh();
    }

    @FXML
    public void acaoAbrirFormulario() {
        painelMenuVendas.setVisible(false);
        painelMenuVendas.setManaged(false);
        painelFormulario.setVisible(true);
        painelFormulario.setManaged(true);
        lblStatus.setText("");
    }

    @FXML
    public void acaoVoltarMenuVendas() {
        painelFormulario.setVisible(false);
        painelFormulario.setManaged(false);
        painelMenuVendas.setVisible(true);
        painelMenuVendas.setManaged(true);
        limparCamposFormulario();
    }

    @FXML
    public void botaoConfirmarVenda() {
        Cliente c = comboCliente.getValue();
        Veiculo v = comboVeiculo.getValue();
        Vendedor vend = comboVendedor.getValue();
        String entradaStr = txtEntrada.getText().trim();

        if (c == null || v == null || vend == null || entradaStr.isEmpty()) {
            lblStatus.setText("❌ ERRO: Preencha todos os campos obrigatórios!");
            lblStatus.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            double entrada = Double.parseDouble(entradaStr);

            // 🟢 MODIFICAÇÃO: Chama efetuarVenda sem passar data. O sistema assume LocalDateTime.now()
            boolean sucesso = control.efetuarVenda(0, c.getCpf(), v.getChassi(), vend.getNome(), entrada);

            if (sucesso) {
                lblStatus.setText("✓ Venda realizada com sucesso de forma automatizada!");
                lblStatus.setStyle("-fx-text-fill: green;");
                limparCamposFormulario();
                atualizarTabelaVendas(control.listarTodasVendas());
            } else {
                lblStatus.setText("❌ ERRO: Não foi possivel realizar venda! Tente Entrada mínima de R$ 5.000,00 | Esse veículo já foi reservado/vendido ou RENAVAM está pendente.");
                lblStatus.setStyle("-fx-text-fill: red;");
            }
        } catch (NumberFormatException e) {
            lblStatus.setText("⚠️ ERRO: Insira uma entrada numérica válida.");
            lblStatus.setStyle("-fx-text-fill: orange;");
        }
    }

    @FXML
    public void acaoFiltrarPorPeriodo() {
        LocalDate inicio = dpFiltroInicio.getValue();
        LocalDate fim = dpFiltroFim.getValue();

        if (inicio == null || fim == null) {
            lblStatus.setText("⚠️ ERRO: Selecione as datas de Início e Fim para o filtro!");
            lblStatus.setStyle("-fx-text-fill: orange;");
            return;
        }

        if (inicio.isAfter(fim)) {
            lblStatus.setText("⚠️ ERRO: A data de início não pode ser maior que a data fim!");
            lblStatus.setStyle("-fx-text-fill: orange;");
            return;
        }

        List<Venda> todas = control.listarTodasVendas();
        List<Venda> filtradas = new ArrayList<>();

        for (Venda v : todas) {
            if (v.getDataVenda() != null) {
                LocalDate dataVendaLD = v.getDataVenda().toLocalDate();
                
                if ((dataVendaLD.isAfter(inicio) || dataVendaLD.isEqual(inicio)) && 
                    (dataVendaLD.isBefore(fim) || dataVendaLD.isEqual(fim))) {
                    filtradas.add(v);
                }
            }
        }

        atualizarTabelaVendas(filtradas);
        lblStatus.setText("✓ Relatório gerado: " + filtradas.size() + " venda(s) encontrada(s).");
        lblStatus.setStyle("-fx-text-fill: blue;");
    }

    @FXML
    public void acaoLimparFiltro() {
        dpFiltroInicio.setValue(null);
        dpFiltroFim.setValue(null);
        lblStatus.setText("");
        if (control != null) {
            atualizarTabelaVendas(control.listarTodasVendas());
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
        if (comboCliente != null) {
            comboCliente.setValue(null);
            comboCliente.setPromptText(""); // Truque para limpar o cache visual anterior
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
