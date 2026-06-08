package br.ufrpe.autodrive.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import br.ufrpe.autodrive.negocio.IGerenciadorOficina;
import br.ufrpe.autodrive.negocio.beans.Cliente;
import br.ufrpe.autodrive.negocio.beans.OrdemServico;
import br.ufrpe.autodrive.negocio.beans.StatusOS;
import br.ufrpe.autodrive.dados.RepositorioClientesArray;
import br.ufrpe.autodrive.dados.RepositorioOsArray;

import java.util.List;
import java.util.stream.Collectors;

public class TelaOficina {
    
    private IGerenciadorOficina control;

    // Componentes de Abertura
    @FXML private TableView<Cliente> tbClientes;
    @FXML private TableColumn<Cliente, String> colClienteNome;
    @FXML private TableColumn<Cliente, String> colClienteCpf;
    @FXML private TextField txtChassi;
    
    // Componentes de Finalização
    @FXML private ComboBox<String> cbOSManutencao;
    
    // Componentes de Tabelas e Filtros
    @FXML private TextField txtFiltroData;
    
    @FXML private TableView<OrdemServico> tbFila;
    @FXML private TableColumn<OrdemServico, String> colFilaOS;
    @FXML private TableColumn<OrdemServico, String> colFilaStatus;
    @FXML private TableColumn<OrdemServico, String> colFilaData;

    @FXML private TableView<OrdemServico> tbHistorico;
    @FXML private TableColumn<OrdemServico, String> colHistOS;
    @FXML private TableColumn<OrdemServico, String> colHistStatus;
    @FXML private TableColumn<OrdemServico, String> colHistData;

    @FXML private Label lblMensagem;

    public TelaOficina() {}

    @FXML
    public void initialize() {
        // Configurando a Tabela de Clientes
        colClienteNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        colClienteCpf.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCpf()));

        // Configurando a Tabela de Fila e Manutenção
        colFilaOS.setCellValueFactory(cellData -> formatarNomeOS(cellData.getValue()));
        colFilaStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().toString()));
        colFilaData.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDataAbertura()));

        // Configurando a Tabela de Histórico
        colHistOS.setCellValueFactory(cellData -> formatarNomeOS(cellData.getValue()));
        colHistStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().toString()));
        colHistData.setCellValueFactory(cellData -> {
            String fechamento = cellData.getValue().getDataFechamento();
            return new SimpleStringProperty(fechamento != null ? fechamento : "N/A");
        });

        // Adiciona listener para recarregar tabelas sempre que o filtro for alterado
        txtFiltroData.textProperty().addListener((observable, oldValue, newValue) -> atualizarDadosTela());
    }

    public void injetarGerenciador(IGerenciadorOficina control) {
        this.control = control;
        atualizarDadosTela();
    }

    @FXML
    public void botaoAbrirOS() { 
        Cliente clienteSelecionado = tbClientes.getSelectionModel().getSelectedItem();
        String chassi = txtChassi.getText().trim();

        if (clienteSelecionado == null) {
            mostrarMensagem("X Erro: Selecione um cliente na tabela.", "red");
            return;
        }

        if (chassi.isEmpty()) {
            mostrarMensagem("X Erro: O campo de chassi é obrigatório.", "red");
            return;
        }

        if (control != null && control.abrirOS(clienteSelecionado.getCpf(), chassi)) {
            mostrarMensagem("✓ Sucesso: OS aberta e enviada para a fila.", "green");
            txtChassi.clear();
            tbClientes.getSelectionModel().clearSelection();
            atualizarDadosTela(); // Recarrega as tabelas para exibir a nova OS
        } else {
            mostrarMensagem("X Erro: Veículo não encontrado ou dados inválidos.", "red");
        }
    }

    @FXML
    public void botaoFinalizarOS() { 
        String osSelecionada = cbOSManutencao.getValue();

        if (osSelecionada == null || osSelecionada.isEmpty()) {
            mostrarMensagem("X Erro: Selecione uma OS para finalizar.", "red");
            return;
        }

        try {
            // Extrai apenas o número da String formatada (ex: "#12345 - João")
            int numero = Integer.parseInt(osSelecionada.substring(1, osSelecionada.indexOf(" -")));

            if (control != null && control.finalizarServico(numero)) {
                mostrarMensagem("✓ Sucesso: OS " + numero + " finalizada.", "green");
                cbOSManutencao.getSelectionModel().clearSelection();
                atualizarDadosTela(); // Atualiza tabelas para mover do quadro ativo pro histórico
            } else {
                mostrarMensagem("X Erro: Não foi possível finalizar a OS.", "red");
            }
        } catch (Exception e) {
            mostrarMensagem("X Erro ao processar o número da OS.", "red");
        }
    }

    @FXML
    public void botaoVoltar() { 
        limparTudo(); 
        ScreenManager.getInstance().showMenuPrincipal();
    }

    // =================================================================================
    // MÉTODOS UTILITÁRIOS E DE ATUALIZAÇÃO DA INTERFACE
    // =================================================================================

    /**
     * Atualiza as Tabelas (Clientes, Fila, Histórico) e o ComboBox de finalização
     */
    private void atualizarDadosTela() {
        String filtroData = txtFiltroData.getText() != null ? txtFiltroData.getText().trim() : "";

        // 1. Carregar Clientes (Bypass simplificado para interface de leitura via Repositorio)
        // OBS: Certifique-se que RepositorioClientesArray tenha o método listarTodos()
        List<Cliente> clientes = RepositorioClientesArray.getInstance().listarTodos();
        tbClientes.setItems(FXCollections.observableArrayList(clientes));

        // 2. Carregar e Filtrar Ordens de Serviço
        List<OrdemServico> todasOS = RepositorioOsArray.getInstance().listarTodas();
        if (todasOS == null) return;

        // Tabela Fila/Manutenção: Status ABERTA ou PROCESSO_MANUTENCAO
        List<OrdemServico> filaAtiva = todasOS.stream()
            .filter(os -> os.getStatus() == StatusOS.ABERTA || os.getStatus() == StatusOS.PROCESSO_MANUTENCAO)
            .filter(os -> filtroData.isEmpty() || (os.getDataAbertura() != null && os.getDataAbertura().contains(filtroData)))
            .sorted((os1, os2) -> {
                // Prioriza quem está em manutenção para o topo da lista
                if (os1.getStatus() == StatusOS.PROCESSO_MANUTENCAO && os2.getStatus() != StatusOS.PROCESSO_MANUTENCAO) return -1;
                if (os1.getStatus() != StatusOS.PROCESSO_MANUTENCAO && os2.getStatus() == StatusOS.PROCESSO_MANUTENCAO) return 1;
                
                // Em caso de empate (ambos abertos ou manutenção), ordena por número de criação cronológica
                return Integer.compare(os1.getNumero(), os2.getNumero());
            })
            .collect(Collectors.toList());
        tbFila.setItems(FXCollections.observableArrayList(filaAtiva));

        // Tabela Histórico: Status FINALIZADA ou PAGA
        List<OrdemServico> historico = todasOS.stream()
            .filter(os -> os.getStatus() == StatusOS.FINALIZADA || os.getStatus() == StatusOS.PAGA)
            .filter(os -> filtroData.isEmpty() || (os.getDataFechamento() != null && os.getDataFechamento().contains(filtroData)))
            .collect(Collectors.toList());
        tbHistorico.setItems(FXCollections.observableArrayList(historico));

        // 3. Atualizar o ComboBox de Finalização (Apenas os que estão com mecânico alocado)
        List<String> comboOptions = todasOS.stream()
            .filter(os -> os.getStatus() == StatusOS.PROCESSO_MANUTENCAO)
            .map(os -> "#" + os.getNumero() + " - " + (os.getCliente() != null ? os.getCliente().getNome() : "Desconhecido"))
            .collect(Collectors.toList());
        cbOSManutencao.setItems(FXCollections.observableArrayList(comboOptions));
    }

    private SimpleStringProperty formatarNomeOS(OrdemServico os) {
        String nome = os.getCliente() != null ? os.getCliente().getNome() : "Desconhecido";
        return new SimpleStringProperty("#" + os.getNumero() + " - " + nome);
    }

    private void mostrarMensagem(String texto, String cor) {
        lblMensagem.setText(texto);
        lblMensagem.setStyle("-fx-text-fill: " + cor + ";");
    }

    private void limparTudo() {
        txtChassi.clear();
        tbClientes.getSelectionModel().clearSelection();
        cbOSManutencao.getSelectionModel().clearSelection();
        txtFiltroData.clear();
        mostrarMensagem("Pronto para operar", "#7f8c8d");
    }
}
