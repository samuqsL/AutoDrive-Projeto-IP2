package br.ufrpe.autodrive.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import br.ufrpe.autodrive.negocio.IGerenciadorOficina;
import br.ufrpe.autodrive.dados.RepositorioOsArray;
import br.ufrpe.autodrive.negocio.beans.OrdemServico;
import br.ufrpe.autodrive.negocio.beans.StatusOS;
import java.util.List;
import java.util.stream.Collectors;

public class TelaOficina {
    
    private IGerenciadorOficina control;

    // --- Componentes do Formulário de Cadastro ---
    @FXML private TextField txtNumeroOS;
    @FXML private TextField txtData;
    @FXML private TextField txtCpf;
    @FXML private TextField txtChassi;
    @FXML private Label lblMensagem;

    // --- Componente de Finalização (Agora é um ComboBox) ---
    @FXML private ComboBox<String> cbFinalizarOS;

    // --- Componentes da Tabela: Fila de Espera / Manutenção ---
    @FXML private TableView<OrdemServico> tbFila;
    @FXML private TableColumn<OrdemServico, String> colFilaOS;
    @FXML private TableColumn<OrdemServico, StatusOS> colFilaStatus;
    @FXML private TableColumn<OrdemServico, String> colFilaData;

    // --- Componentes da Tabela: Histórico ---
    @FXML private TableView<OrdemServico> tbHistorico;
    @FXML private TableColumn<OrdemServico, String> colHistOS;
    @FXML private TableColumn<OrdemServico, StatusOS> colHistStatus;
    @FXML private TableColumn<OrdemServico, String> colHistData;

    public TelaOficina() {}

    @FXML
    public void initialize() {
        // Vincula dinamicamente a Fila (OS + Nome)
        if (colFilaOS != null) {
            colFilaOS.setCellValueFactory(data -> {
                OrdemServico os = data.getValue();
                String clienteNome = (os != null && os.getCliente() != null) ? os.getCliente().getNome() : "Desconhecido";
                return new javafx.beans.property.SimpleStringProperty(os.getNumero() + " - " + clienteNome);
            });
        }
        if (colFilaStatus != null) colFilaStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        if (colFilaData != null) colFilaData.setCellValueFactory(new PropertyValueFactory<>("dataAbertura"));

        // Vincula dinamicamente o Histórico (OS + Nome)
        if (colHistOS != null) {
            colHistOS.setCellValueFactory(data -> {
                OrdemServico os = data.getValue();
                String clienteNome = (os != null && os.getCliente() != null) ? os.getCliente().getNome() : "Desconhecido";
                return new javafx.beans.property.SimpleStringProperty(os.getNumero() + " - " + clienteNome);
            });
        }
        if (colHistStatus != null) colHistStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        if (colHistData != null) colHistData.setCellValueFactory(new PropertyValueFactory<>("dataFechamento"));
    }

    public void injetarGerenciador(IGerenciadorOficina control) {
        this.control = control;
        atualizarTabelas();
    }

    @FXML
    public void botaoAbrirOS() { 
        try {
            String cpf = txtCpf.getText().trim();
            String chassi = txtChassi.getText().trim();

            if (cpf.isEmpty() || chassi.isEmpty()) {
                exibirMensagemErro("O CPF do cliente e o Chassi do veículo são obrigatórios.");
                return;
            }

            if (control != null && control.abrirOS(cpf, chassi)) {
                lblMensagem.setText("✓ Sucesso: Nova Ordem de Serviço inserida na Fila.");
                lblMensagem.setStyle("-fx-text-fill: green;");
                limparCamposCadastro();
                atualizarTabelas();
            } else {
                exibirMensagemErro("Não foi possível abrir a OS. Verifique os dados.");
            }
        } catch (Exception e) {
            exibirMensagemErro("Ocorreu uma falha ao tentar abrir a Ordem de Serviço.");
        }
    }

    @FXML
    public void botaoFinalizarOS() {
        try {
            // Pega a opção que o gerente selecionou no ComboBox
            String selecao = cbFinalizarOS.getValue();
            
            if (selecao == null || selecao.trim().isEmpty()) {
                exibirMensagemErro("Selecione uma OS em manutenção para finalizar.");
                return;
            }

            // A seleção vai estar no formato "12345 - Nome". Vamos extrair só o número (antes do espaço).
            int numeroOS = Integer.parseInt(selecao.split(" ")[0]);

            if (control != null && control.finalizarServico(numeroOS)) {
                lblMensagem.setText("✓ Sucesso: OS nº " + numeroOS + " finalizada. Mecânico e Veículo liberados.");
                lblMensagem.setStyle("-fx-text-fill: green;");
                atualizarTabelas(); // Atualiza tabelas e limpa o ComboBox
            } else {
                exibirMensagemErro("Falha ao finalizar a OS selecionada.");
            }
        } catch (Exception e) {
            exibirMensagemErro("Falha operacional ao encerrar a Ordem de Serviço.");
        }
    }

    private void atualizarTabelas() {
        try {
            List<OrdemServico> todasOS = RepositorioOsArray.getInstance().listarTodas();
            if (todasOS == null) return;

            // 1. Atualiza Fila (ABERTA e PROCESSO_MANUTENCAO)
            List<OrdemServico> filaAtiva = todasOS.stream()
                .filter(os -> os.getStatus() == StatusOS.ABERTA || os.getStatus() == StatusOS.PROCESSO_MANUTENCAO)
                .collect(Collectors.toList());

            // 2. Atualiza Histórico (FINALIZADA, PAGA, etc)
            List<OrdemServico> historicoConcluido = todasOS.stream()
                .filter(os -> os.getStatus() != StatusOS.ABERTA && os.getStatus() != StatusOS.PROCESSO_MANUTENCAO)
                .collect(Collectors.toList());

            // 3. Atualiza o ComboBox de Finalizar (APENAS PROCESSO_MANUTENCAO)
            List<String> opcoesFinalizar = todasOS.stream()
                .filter(os -> os.getStatus() == StatusOS.PROCESSO_MANUTENCAO)
                .map(os -> {
                    String nome = (os.getCliente() != null) ? os.getCliente().getNome() : "Desconhecido";
                    return os.getNumero() + " - " + nome; // Ex: "54321 - João"
                })
                .collect(Collectors.toList());

            if (tbFila != null) {
                tbFila.setItems(FXCollections.observableArrayList(filaAtiva));
                tbFila.refresh();
            }
            if (tbHistorico != null) {
                tbHistorico.setItems(FXCollections.observableArrayList(historicoConcluido));
                tbHistorico.refresh();
            }
            if (cbFinalizarOS != null) {
                cbFinalizarOS.setItems(FXCollections.observableArrayList(opcoesFinalizar));
                cbFinalizarOS.getSelectionModel().clearSelection(); // Limpa a seleção anterior
            }
        } catch (Exception e) {
            System.err.println("Erro crítico ao sincronizar dados: " + e.getMessage());
        }
    }

    @FXML
    public void botaoVoltar() { 
        limparTudo(); 
        ScreenManager.getInstance().showMenuPrincipal();
    }

    private void limparCamposCadastro() {
        if (txtNumeroOS != null) txtNumeroOS.clear();
        if (txtData != null) txtData.clear();
        if (txtCpf != null) txtCpf.clear();
        if (txtChassi != null) txtChassi.clear();
    }

    private void limparTudo() {
        limparCamposCadastro();      
        if (cbFinalizarOS != null) cbFinalizarOS.getSelectionModel().clearSelection();     
        if (lblMensagem != null) {
            lblMensagem.setText("Pronto para operar"); 
            lblMensagem.setStyle("-fx-text-fill: black;"); 
        }
    }

    private void exibirMensagemErro(String msg) {
        if (lblMensagem != null) {
            lblMensagem.setText("X Erro: " + msg);
            lblMensagem.setStyle("-fx-text-fill: red;");
        }
    }
}
