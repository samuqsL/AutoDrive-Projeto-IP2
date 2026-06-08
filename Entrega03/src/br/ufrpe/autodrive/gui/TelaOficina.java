package br.ufrpe.autodrive.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import br.ufrpe.autodrive.negocio.IGerenciadorOficina;
import br.ufrpe.autodrive.negocio.beans.OrdemServico;
import br.ufrpe.autodrive.negocio.beans.StatusOS;
import br.ufrpe.autodrive.dados.RepositorioOsArray;

import java.util.List;
import java.util.stream.Collectors;

public class TelaOficina {
    
    private IGerenciadorOficina control;

    // Campos de Entrada
    @FXML private TextField txtCpf;
    @FXML private TextField txtChassi;
    @FXML private TextField txtFinalizarOS;
    @FXML private Label lblMensagem;

    // Tabela 1: Fila de Espera / Em Manutenção
    @FXML private TableView<OrdemServico> tbFila;
    @FXML private TableColumn<OrdemServico, String> colFilaOS;
    @FXML private TableColumn<OrdemServico, String> colFilaCliente;
    @FXML private TableColumn<OrdemServico, String> colFilaVeiculo;
    @FXML private TableColumn<OrdemServico, String> colFilaMecanico;
    @FXML private TableColumn<OrdemServico, String> colFilaStatus;

    // Tabela 2: Histórico de Concluídas
    @FXML private TableView<OrdemServico> tbHistorico;
    @FXML private TableColumn<OrdemServico, String> colHistOS;
    @FXML private TableColumn<OrdemServico, String> colHistStatus;
    @FXML private TableColumn<OrdemServico, String> colHistData;

    public TelaOficina() {}

    public void injetarGerenciador(IGerenciadorOficina control) {
        this.control = control;
        atualizarTabelas(); // Carrega os dados assim que o gerenciador for injetado
    }

    @FXML
    public void initialize() {
        // Configuração das Colunas da Tabela da Fila Ativa
        colFilaOS.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getNumero())));
        colFilaCliente.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getCliente() != null ? data.getValue().getCliente().getNome() : "Não Informado"
        ));
        colFilaVeiculo.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getVeiculo() != null ? data.getValue().getVeiculo().getModelo() : "Não Informado"
        ));
        colFilaMecanico.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getMecanico() != null ? data.getValue().getMecanico().getNome() : "Aguardando..."
        ));
        colFilaStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().toString()));

        // Configuração das Colunas da Tabela do Histórico
        colHistOS.setCellValueFactory(data -> new SimpleStringProperty(
            "OS #" + data.getValue().getNumero() + " - " + 
            (data.getValue().getCliente() != null ? data.getValue().getCliente().getNome() : "Cliente")
        ));
        colHistStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().toString()));
        colHistData.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getDataFechamento() != null ? data.getValue().getDataFechamento() : "---"
        ));
    }

    @FXML
    public void botaoAbrirOS() { 
        try {
            String cpf = txtCpf.getText().trim();
            String chassi = txtChassi.getText().trim();

            if (cpf.isEmpty() || chassi.isEmpty()) {
                lblMensagem.setText("X Erro: CPF do Cliente e Chassi do Veículo são obrigatórios.");
                lblMensagem.setStyle("-fx-text-fill: red;");
                return;
            }

            if (control != null) {
                boolean sucesso = control.abrirOS(cpf, chassi);
                if (sucesso) {
                    lblMensagem.setText("✓ Sucesso: Nova Ordem de Serviço aberta e adicionada ao fluxo!");
                    lblMensagem.setStyle("-fx-text-fill: green;");
                    limparCamposCadastro();
                    atualizarTabelas();
                } else {
                    lblMensagem.setText("X Erro: Não foi possível abrir a OS. Verifique o CPF e o Chassi.");
                    lblMensagem.setStyle("-fx-text-fill: red;");
                }
            }
        } catch (Exception e) {
            lblMensagem.setText("X Erro inesperado: " + e.getMessage());
            lblMensagem.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void botaoFinalizarOS() {
        try {
            if (txtFinalizarOS.getText().trim().isEmpty()) {
                lblMensagem.setText("X Erro: Digite o número da OS para finalizar.");
                lblMensagem.setStyle("-fx-text-fill: red;");
                return;
            }

            int numero = Integer.parseInt(txtFinalizarOS.getText().trim());

            if (control != null && control.finalizarServico(numero)) {
                lblMensagem.setText("✓ Sucesso: OS " + numero + " finalizada. Mecânico liberado!");
                lblMensagem.setStyle("-fx-text-fill: green;");
                txtFinalizarOS.clear();
                atualizarTabelas();
            } else {
                lblMensagem.setText("X Erro: OS não encontrada ou requisitos não atendidos.");
                lblMensagem.setStyle("-fx-text-fill: red;");
            }
        } catch (NumberFormatException e) {
            lblMensagem.setText("X Erro: Digite um número de OS válido.");
            lblMensagem.setStyle("-fx-text-fill: red;");
        }
    }

    public void atualizarTabelas() {
        List<OrdemServico> todasAsOS = RepositorioOsArray.getInstance().listarTodas();

        // Filtra as ordens que estão na fila de espera ou em manutenção
        List<OrdemServico> filaAtiva = todasAsOS.stream()
            .filter(os -> os.getStatus() == StatusOS.ABERTA || os.getStatus() == StatusOS.PROCESSO_MANUTENCAO)
            .collect(Collectors.toList());

        // Filtra as ordens concluídas
        List<OrdemServico> historicoConcluido = todasAsOS.stream()
            .filter(os -> os.getStatus() == StatusOS.FINALIZADA || os.getStatus() == StatusOS.PAGA)
            .collect(Collectors.toList());

        tbFila.setItems(FXCollections.observableArrayList(filaAtiva));
        tbHistorico.setItems(FXCollections.observableArrayList(historicoConcluido));
    }

    @FXML
    public void botaoVoltar() { 
        limparTudo(); 
        ScreenManager.getInstance().showMenuPrincipal();
    }

    private void limparCamposCadastro() {
        txtCpf.clear();
        txtChassi.clear();
    }

    private void limparTudo() {
        limparCamposCadastro();
        txtFinalizarOS.clear();
        lblMensagem.setText("Pronto para operar");
        lblMensagem.setStyle("-fx-text-fill: black;");
    }
}
