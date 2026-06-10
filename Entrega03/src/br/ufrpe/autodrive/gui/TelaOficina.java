package br.ufrpe.autodrive.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import br.ufrpe.autodrive.negocio.IGerenciadorOficina;
import br.ufrpe.autodrive.dados.RepositorioOsArray;
import br.ufrpe.autodrive.dados.RepositorioClientesArray;
import br.ufrpe.autodrive.dados.RepositorioVeiculosArray;
import br.ufrpe.autodrive.negocio.beans.OrdemServico;
import br.ufrpe.autodrive.negocio.beans.StatusOS;
import br.ufrpe.autodrive.negocio.beans.Cliente;
import br.ufrpe.autodrive.negocio.beans.Veiculo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TelaOficina {
    
    private IGerenciadorOficina control;

    // --- Componentes do Formulário de Cadastro ---
    @FXML private ComboBox<Cliente> cbCliente;
    @FXML private ComboBox<Veiculo> cbVeiculo;
    @FXML private Label lblMensagem;

    // --- Componente de Finalização ---
    @FXML private ComboBox<String> cbFinalizarOS;

    // --- Componentes de Filtro (Substituindo TextField por DatePicker) ---
    @FXML private DatePicker dpFiltroData;

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

        // Efeito Cascata do DatePicker: Atualiza tabelas instantaneamente ao selecionar uma data
        if (dpFiltroData != null) {
            dpFiltroData.valueProperty().addListener((observable, oldValue, newValue) -> {
                atualizarTabelas();
            });
        }
    }

    public void injetarGerenciador(IGerenciadorOficina control) {
        this.control = control;
        carregarComboBoxes();
        atualizarTabelas();
    }

    // Carrega os dados dos repositórios diretamente para os ComboBoxes
    private void carregarComboBoxes() {
        try {
            List<Cliente> clientes = RepositorioClientesArray.getInstance().listarClientes();
            if (cbCliente != null) cbCliente.setItems(FXCollections.observableArrayList(clientes));

            List<Veiculo> veiculos = RepositorioVeiculosArray.getInstance().listarTodos();
            if (cbVeiculo != null) cbVeiculo.setItems(FXCollections.observableArrayList(veiculos));
        } catch (Exception e) {
            System.err.println("Aviso: Falha ao carregar repositórios nos ComboBoxes.");
        }
    }

    @FXML
    public void botaoAbrirOS() { 
        try {
            Cliente clienteSelecionado = cbCliente.getValue();
            Veiculo veiculoSelecionado = cbVeiculo.getValue();

            // Validação direta baseada nas seleções
            if (clienteSelecionado == null || veiculoSelecionado == null) {
                exibirMensagemErro("Selecione um Cliente e um Veículo para abrir a OS.");
                return;
            }

            // Extrai as chaves necessárias para o Gerenciador (Sem precisar alterar a interface lógica)
            String cpf = clienteSelecionado.getCpf();
            String chassi = veiculoSelecionado.getChassi();

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
            String selecao = cbFinalizarOS.getValue();
            
            if (selecao == null || selecao.trim().isEmpty()) {
                exibirMensagemErro("Selecione uma OS em manutenção para finalizar.");
                return;
            }

            int numeroOS = Integer.parseInt(selecao.split(" ")[0]);

            if (control != null && control.finalizarServico(numeroOS)) {
                lblMensagem.setText("✓ Sucesso: OS nº " + numeroOS + " finalizada. Mecânico e Veículo liberados.");
                lblMensagem.setStyle("-fx-text-fill: green;");
                atualizarTabelas(); 
            } else {
                exibirMensagemErro("Falha ao finalizar a OS selecionada.");
            }
        } catch (Exception e) {
            exibirMensagemErro("Falha operacional ao encerrar a Ordem de Serviço.");
        }
    }

    // Método atrelado ao novo botão de limpar o filtro de data
    @FXML
    public void limparFiltroData() {
        if (dpFiltroData != null) {
            dpFiltroData.setValue(null); // O Listener já vai capturar isso e resetar a tabela
        }
    }

    private void atualizarTabelas() {
        try {
            List<OrdemServico> todasOS = RepositorioOsArray.getInstance().listarTodas();
            if (todasOS == null) return;

            LocalDate dataFiltro = (dpFiltroData != null) ? dpFiltroData.getValue() : null;

            // 1. Atualiza Fila (Aplica filtro de Data simultaneamente)
            List<OrdemServico> filaAtiva = todasOS.stream()
                .filter(os -> os.getStatus() == StatusOS.ABERTA || os.getStatus() == StatusOS.PROCESSO_MANUTENCAO)
                .filter(os -> verificaDataFiltro(os.getDataAbertura(), dataFiltro))
                .collect(Collectors.toList());

            // 2. Atualiza Histórico (Aplica filtro de Data simultaneamente)
            List<OrdemServico> historicoConcluido = todasOS.stream()
                .filter(os -> os.getStatus() != StatusOS.ABERTA && os.getStatus() != StatusOS.PROCESSO_MANUTENCAO)
                .filter(os -> verificaDataFiltro(os.getDataFechamento(), dataFiltro))
                .collect(Collectors.toList());

            // 3. Atualiza o ComboBox de Finalizar (APENAS PROCESSO_MANUTENCAO)
            List<String> opcoesFinalizar = todasOS.stream()
                .filter(os -> os.getStatus() == StatusOS.PROCESSO_MANUTENCAO)
                .map(os -> {
                    String nome = (os.getCliente() != null) ? os.getCliente().getNome() : "Desconhecido";
                    return os.getNumero() + " - " + nome; 
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
            }
        } catch (Exception e) {
            System.err.println("Erro crítico ao sincronizar dados: " + e.getMessage());
        }
    }

    // 🟢 CORREÇÃO: Método adaptado para ler a String (dd/MM/yyyy) gerada na OrdemServico
    private boolean verificaDataFiltro(Object dataObjeto, LocalDate dataFiltro) {
        if (dataFiltro == null) return true; // Se não tem filtro selecionado, mostra tudo
        if (dataObjeto == null) return false;

        try {
            String dataStr = dataObjeto.toString();
            // Se a data contiver hora (ex: "10/06/2026 14:30"), pega apenas a parte da data
            if (dataStr.contains(" ")) {
                dataStr = dataStr.split(" ")[0];
            }
            
            // Converte a string "dd/MM/yyyy" da OS para o padrão LocalDate para comparar perfeitamente
            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dataConvertida = LocalDate.parse(dataStr, formatador);
            
            return dataConvertida.equals(dataFiltro);
        } catch (Exception e) {
            // Fallback de segurança caso a string esteja em outro formato
            return dataObjeto.toString().contains(dataFiltro.toString());
        }
    }

    @FXML
    public void botaoVoltar() { 
        limparTudo(); 
        ScreenManager.getInstance().showMenuPrincipal();
    }

    private void limparCamposCadastro() {
        if (cbCliente != null) cbCliente.getSelectionModel().clearSelection();
        if (cbVeiculo != null) cbVeiculo.getSelectionModel().clearSelection();
    }

    private void limparTudo() {
        limparCamposCadastro();      
        if (dpFiltroData != null) dpFiltroData.setValue(null);
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
