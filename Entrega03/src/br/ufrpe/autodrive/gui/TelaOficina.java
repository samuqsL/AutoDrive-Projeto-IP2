package br.ufrpe.autodrive.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import br.ufrpe.autodrive.negocio.IGerenciadorOficina;
import br.ufrpe.autodrive.negocio.beans.OrdemServico;

import java.util.List;
import java.util.stream.Collectors;

public class TelaOficina {
    
    private IGerenciadorOficina control;

    // --- Componentes do Formulário (Quadrante Esquerdo) ---
    @FXML private TextField txtNumeroOS;
    @FXML private TextField txtData;
    @FXML private TextField txtCpf;
    @FXML private TextField txtChassi;
    @FXML private TextField txtFinalizarOS;
    @FXML private Label lblMensagem;

    // --- Componentes das Tabelas (Quadrante Direito) ---
    // Tabela 1: Fila de Espera / Em Manutenção (Parte Superior)
    @FXML private TableView<OrdemServico> tbFila; 
    @FXML private TableColumn<OrdemServico, Integer> colFilaNumero;
    @FXML private TableColumn<OrdemServico, String> colFilaCliente;
    @FXML private TableColumn<OrdemServico, String> colFilaStatus;

    // Tabela 2: Histórico de Finalizadas e Pagas (Parte Inferior)
    @FXML private TableView<OrdemServico> tbHistorico;
    @FXML private TableColumn<OrdemServico, String> colHistOS;
    @FXML private TableColumn<OrdemServico, String> colHistStatus;
    @FXML private TableColumn<OrdemServico, String> colHistData;

    // Listas observáveis exigidas pelo JavaFX para monitorar e renderizar os dados
    private ObservableList<OrdemServico> obsListFila = FXCollections.observableArrayList();
    private ObservableList<OrdemServico> obsListHistorico = FXCollections.observableArrayList();

    public TelaOficina() {}

    /**
     * Injeta o gerenciador de negócios da oficina e inicializa o preenchimento das tabelas.
     */
    public void injetarGerenciador(IGerenciadorOficina control) {
        this.control = control;
        atualizarTabelas();
    }

    /**
     * Método executado automaticamente pelo JavaFX assim que o componente FXML termina de carregar.
     * Mapeia os atributos da classe OrdemServico para as colunas visuais da interface.
     */
    @FXML
    public void initialize() {
        // Vinculação da Tabela da Fila (Status: ABERTA / PROCESSO_MANUTENCAO)
        if (colFilaNumero != null) colFilaNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        if (colFilaCliente != null) colFilaCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        if (colFilaStatus != null) colFilaStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        if (tbFila != null) tbFila.setItems(obsListFila);

        // Vinculação da Tabela do Histórico (Status: FINALIZADA / PAGO)
        if (colHistOS != null) colHistOS.setCellValueFactory(new PropertyValueFactory<>("numero"));
        if (colHistStatus != null) colHistStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        if (colHistData != null) colHistData.setCellValueFactory(new PropertyValueFactory<>("dataFechamento"));
        if (tbHistorico != null) tbHistorico.setItems(obsListHistorico);
    }

    /**
     * Consulta o GerenciadorOficina, filtra as ordens por categoria e atualiza a exibição na tela.
     */
    public void atualizarTabelas() {
        if (control == null) return;

        // Recupera a listagem completa através do método unificado exposto pela interface de negócio
        List<OrdemServico> todasAsOS = control.listarTodasOS(); 
        if (todasAsOS != null) {
            
            // 1. Filtra as ordens ativas que estão aguardando ou em execução mecânica
            List<OrdemServico> fila = todasAsOS.stream()
                .filter(os -> os.getStatus() != null && 
                       (os.getStatus().toString().equals("ABERTA") || os.getStatus().toString().equals("PROCESSO_MANUTENCAO")))
                .collect(Collectors.toList());
            obsListFila.setAll(fila);

            // 2. Filtra as ordens concluídas (Finalizadas prontas para entrega ou já Pagas)
            List<OrdemServico> historico = todasAsOS.stream()
                .filter(os -> os.getStatus() != null && 
                       (os.getStatus().toString().equals("FINALIZADA") || os.getStatus().toString().equals("PAGO")))
                .collect(Collectors.toList());
            obsListHistorico.setAll(historico);
        }
    }

    /**
     * Captura os inputs de texto, valida o preenchimento e solicita a abertura da OS no negócio.
     */
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

            // O número da OS e a Data de Abertura são gerados de forma automatizada no construtor da OrdemServico.
            if (control != null && control.abrirOS(cpf, chassi)) {
                lblMensagem.setText("✓ Sucesso: OS aberta e enviada para processamento na fila.");
                lblMensagem.setStyle("-fx-text-fill: green;");
                limparCamposCadastro();
                atualizarTabelas(); 
            } else {
                lblMensagem.setText("X Erro: Não foi possível abrir a OS. Verifique se o CPF ou o Chassi existem.");
                lblMensagem.setStyle("-fx-text-fill: red;");
            }

        } catch (Exception e) {
            lblMensagem.setText("X Erro inesperado: " + e.getMessage());
            lblMensagem.setStyle("-fx-text-fill: red;");
        }
    }
    
    /**
     * Lê o código numérico fornecido e solicita o encerramento dos serviços mecânicos associados.
     */
    @FXML
    public void botaoFinalizarOS() {
        try {
            if (txtFinalizarOS.getText().trim().isEmpty()) {
                lblMensagem.setText("X Erro: O número da OS é obrigatório para a finalização.");
                lblMensagem.setStyle("-fx-text-fill: red;");
                return;
            }
            int numero = Integer.parseInt(txtFinalizarOS.getText().trim());

            if (control != null && control.finalizarServico(numero)) {
                lblMensagem.setText("✓ Sucesso: OS " + numero + " finalizada e Mecânico liberado.");
                lblMensagem.setStyle("-fx-text-fill: green;");
                txtFinalizarOS.clear();
                atualizarTabelas(); 
            } else {
                lblMensagem.setText("X Erro: OS não localizada ou não pôde ser encerrada.");
                lblMensagem.setStyle("-fx-text-fill: red;");
            }
        } catch (NumberFormatException e) {
            lblMensagem.setText("X Erro: Digite um número identificador de OS válido.");
            lblMensagem.setStyle("-fx-text-fill: red;");
        }
    }

    /**
     * Retorna ao Menu Principal do sistema limpando o cache e o estado visual atual da janela.
     */
    @FXML
    public void botaoVoltar() { 
        limparTudo(); 
        ScreenManager.getInstance().showMenuPrincipal();
    }

    /**
     * Reseta exclusivamente as caixas de texto do formulário de inserção.
     */
    private void limparCamposCadastro() {
        if (txtNumeroOS != null) txtNumeroOS.clear();
        if (txtData != null) txtData.clear();
        if (txtCpf != null) txtCpf.clear();
        if (txtChassi != null) txtChassi.clear();
    }

    /**
     * Restaura completamente o estado padrão limpo de todos os elementos visuais de texto.
     */
    private void limparTudo() {
        limparCamposCadastro();      
        if (txtFinalizarOS != null) txtFinalizarOS.clear();      
        if (lblMensagem != null) {
            lblMensagem.setText("Pronto para operar"); 
            lblMensagem.setStyle("-fx-text-fill: black;");
        }
    }
}
