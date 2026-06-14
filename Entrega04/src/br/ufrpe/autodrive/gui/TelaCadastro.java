package br.ufrpe.autodrive.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import br.ufrpe.autodrive.negocio.IGerenciadorCadastro;
import br.ufrpe.autodrive.negocio.beans.Cliente;
import br.ufrpe.autodrive.negocio.beans.Veiculo;
import br.ufrpe.autodrive.negocio.beans.StatusVeiculo;

import java.util.ArrayList;
import java.util.List;

public class TelaCadastro {

    // 1. ATRIBUTO DO GERENCIADOR DE NEGÓCIO
    private IGerenciadorCadastro gCadastro;

    // 2. COMPONENTES DE PAINÉIS (Para alternar a visualização do Hub)
    @FXML private HBox panelHubBotoes;
    @FXML private VBox panelFormCliente;
    @FXML private VBox panelFormVeiculo;

    // 3. CAMPOS DE ENTRADA DO FORMULÁRIO DE CLIENTE
    @FXML private TextField txtNome;
    @FXML private TextField txtCpf;
    @FXML private TextField txtCnh;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefone;

    // 4. CAMPOS DE ENTRADA DO FORMULÁRIO DE VEÍCULO
    @FXML private TextField txtChassi;
    @FXML private TextField txtRenavam;
    @FXML private TextField txtModelo;
    @FXML private TextField txtAno;
    @FXML private TextField txtPreco;
    @FXML private TextField txtKm;
    @FXML private RadioButton radioNovo;
    @FXML private RadioButton radioSeminovo;
    @FXML private ToggleGroup grupoTipoVeiculo;

    // FILTRO DE STATUS
    @FXML private ComboBox<String> comboFiltroStatus;

    // 5. TABELA DE CLIENTES
    @FXML private TableView<Cliente> tabelaClientes;
    @FXML private TableColumn<Cliente, String> colClienteNome;
    @FXML private TableColumn<Cliente, String> colClienteCpf;
    @FXML private TableColumn<Cliente, String> colClienteCnh;

    // 6. TABELA DE VEÍCULOS
    @FXML private TableView<Veiculo> tabelaVeiculos;
    @FXML private TableColumn<Veiculo, String> colVeiculoModelo;
    @FXML private TableColumn<Veiculo, Integer> colVeiculoAno;
    @FXML private TableColumn<Veiculo, Double> colVeiculoPreco;
    @FXML private TableColumn<Veiculo, Double> colVeiculoKm; // Nova Coluna
    @FXML private TableColumn<Veiculo, Object> colVeiculoStatus;

    /**
     * Método automático do JavaFX executado após o FXML carregar.
     * Usado para amarrar as colunas das tabelas e os ouvintes (listeners).
     */
    @FXML
    public void initialize() {
        // Mapeamento das propriedades dos Beans com as colunas da Tabela
        colClienteNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colClienteCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colClienteCnh.setCellValueFactory(new PropertyValueFactory<>("cnh"));

        colVeiculoModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colVeiculoAno.setCellValueFactory(new PropertyValueFactory<>("ano"));
        colVeiculoPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colVeiculoKm.setCellValueFactory(new PropertyValueFactory<>("quilometragem")); // Vincula com getQuilometragem() ou mude para "km" se for o nome exato no Bean
        colVeiculoStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Inicializa a ComboBox com os valores do ENUM StatusVeiculo + Opção de Limpar Filtro
        List<String> opcoesFiltro = new ArrayList<>();
        opcoesFiltro.add("TODOS");
        for (StatusVeiculo status : StatusVeiculo.values()) {
            opcoesFiltro.add(status.name());
        }
        comboFiltroStatus.setItems(FXCollections.observableArrayList(opcoesFiltro));
        comboFiltroStatus.getSelectionModel().selectFirst(); // Começa marcado em "TODOS"

        // LISTENER INTELIGENTE: Liga/Desliga o campo KM baseado no tipo do veículo selecionado
        grupoTipoVeiculo.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle == radioSeminovo) {
                txtKm.setDisable(false);
                txtKm.requestFocus(); // Dá foco direto no campo de quilometragem
            } else {
                txtKm.setDisable(true);
                txtKm.clear(); // Limpa se digitou algo por engano
            }
        });
    }

    /**
     * Método chamado pelo ScreenManager para injetar a regra de negócio vinda da Main
     */
    public void injetarGerenciador(IGerenciadorCadastro gC) {
        this.gCadastro = gC;
        atualizarTabelas(); // Popula as tabelas na inicialização do app
    }

    // =========================================================================
    // LÓGICA DE FILTRAGEM POR STATUS
    // =========================================================================
    
    @FXML
    public void tratarFiltroStatus() {
        atualizarTabelas();
    }

    // =========================================================================
    // LÓGICA DE TRANSIÇÃO VISUAL DOS FORMULÁRIOS
    // =========================================================================
    
    @FXML
    public void exibirFormularioCliente() {
        limparCamposCliente();
        chavearPainel(false, true, false);
    }

    @FXML
    public void exibirFormularioVeiculo() {
        limparCamposVeiculo();
        chavearPainel(false, false, true);
    }

    @FXML
    public void voltarParaHubPrincipal() {
        chavearPainel(true, false, false);
    }

    @FXML
    public void tratarBotaoVoltarMenuPrincipal() {
        voltarParaHubPrincipal(); // Garante o reset visual
        ScreenManager.getInstance().showMenuPrincipal();
    }

    /**
     * Controla quais nós participam do cálculo do layout e estão visíveis
     */
    private void chavearPainel(boolean hub, boolean cliente, boolean veiculo) {
        panelHubBotoes.setVisible(hub);
        panelHubBotoes.setManaged(hub);

        panelFormCliente.setVisible(cliente);
        panelFormCliente.setManaged(cliente);

        panelFormVeiculo.setVisible(veiculo);
        panelFormVeiculo.setManaged(veiculo);
    }

    // =========================================================================
    // TRATAMENTO DAS AÇÕES DE BOTÃO E VALIDAÇÕES DO FX
    // =========================================================================

    @FXML
    public void tratarBotaoCadastrarCliente() {
        try {
            gCadastro.cadastrarCliente(
                txtNome.getText(),
                txtCpf.getText(),
                txtCnh.getText(),
                txtEmail.getText(),
                txtTelefone.getText()
            );

            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso!", "Cliente registrado no banco de dados com êxito.");
            atualizarTabelas();
            voltarParaHubPrincipal();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Falha de Cadastro", e.getMessage());
        }
    }

    @FXML
    public void tratarBotaoCadastrarVeiculo() {
        try {
            String chassi = txtChassi.getText();
            String renavam = txtRenavam.getText();
            String modelo = txtModelo.getText();
            
            int ano = 0;
            if (!txtAno.getText().trim().isEmpty()) {
                try {
                    ano = Integer.parseInt(txtAno.getText().trim());
                } catch (NumberFormatException e) {
                    throw new Exception("O campo 'Ano Fabricação' deve ser um número inteiro válido!");
                }
            }

            double preco = 0.0;
            if (!txtPreco.getText().trim().isEmpty()) {
                try {
                    preco = Double.parseDouble(txtPreco.getText().trim());
                } catch (NumberFormatException e) {
                    throw new Exception("O campo 'Preço Base' deve ser um valor numérico decimal válido (Ex: 85000.00)!");
                }
            }

            boolean ehSeminovo = radioSeminovo.isSelected();
            double km = 0.0;
            
            if (ehSeminovo && !txtKm.getText().trim().isEmpty()) {
                try {
                    km = Double.parseDouble(txtKm.getText().trim());
                } catch (NumberFormatException e) {
                    throw new Exception("A quilometragem inicial digitada é inválida!");
                }
            }

            gCadastro.cadastrarVeiculo(chassi, renavam, modelo, ano, preco, ehSeminovo, km);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso!", "Veículo adicionado ao estoque geral da concessionária.");
            atualizarTabelas();
            voltarParaHubPrincipal();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Falha de Estoque", e.getMessage());
        }
    }

    // =========================================================================
    // MÉTODOS AUXILIARES: LIMPEZA, UTILS E RENDERIZAÇÃO
    // =========================================================================

    private void atualizarTabelas() {
        if (gCadastro != null) {
            tabelaClientes.setItems(FXCollections.observableArrayList(gCadastro.listarClientes()));
            
            // Lógica do Filtro por Status da ComboBox
            List<Veiculo> todosVeiculos = gCadastro.listarVeiculos();
            String statusSelecionado = comboFiltroStatus.getSelectionModel().getSelectedItem();
            
            if (statusSelecionado == null || statusSelecionado.equals("TODOS")) {
                tabelaVeiculos.setItems(FXCollections.observableArrayList(todosVeiculos));
            } else {
                List<Veiculo> filtrados = new ArrayList<>();
                for (Veiculo v : todosVeiculos) {
                    if (v.getStatus() != null && v.getStatus().name().equalsIgnoreCase(statusSelecionado)) {
                        filtrados.add(v);
                    }
                }
                tabelaVeiculos.setItems(FXCollections.observableArrayList(filtrados));
            }

            tabelaClientes.refresh();
            tabelaVeiculos.refresh();
        }
    }

    private void limparCamposCliente() {
        txtNome.clear();
        txtCpf.clear();
        txtCnh.clear();
        txtEmail.clear();
        txtTelefone.clear();
    }

    private void limparCamposVeiculo() {
        txtChassi.clear();
        txtRenavam.clear();
        txtModelo.clear();
        txtAno.clear();
        txtPreco.clear();
        txtKm.clear();
        radioNovo.setSelected(true);
        txtKm.setDisable(true);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
