package br.ufrpe.autodrive.gui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import br.ufrpe.autodrive.dados.RepositorioClientesArray;
import br.ufrpe.autodrive.dados.RepositorioOsArray;
import br.ufrpe.autodrive.dados.RepositorioVeiculosArray;
import br.ufrpe.autodrive.dados.RepositorioPecasArray;
import br.ufrpe.autodrive.negocio.IGerenciadorOficina;
import br.ufrpe.autodrive.negocio.beans.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class TelaOficina {
    
    private IGerenciadorOficina control;

    @FXML private ComboBox<Cliente> cbCliente;
    @FXML private ComboBox<Veiculo> cbVeiculo;
    @FXML private ComboBox<Pecas> cbPecas;
    
    @FXML private TextField txtQuantidadePeca;
    @FXML private TextField txtDescricaoMaoDeObra;
    @FXML private TextField txtValorMaoDeObra;
    
    @FXML private Label lblMensagem;

    @FXML private TableView<OrdemServico> tbFilaEspera;
    @FXML private TableColumn<OrdemServico, String> colFilaOS;
    @FXML private TableColumn<OrdemServico, String> colFilaVeiculo;
    @FXML private TableColumn<OrdemServico, String> colFilaStatus;

    @FXML private TableView<OrdemServico> tbHistorico;
    @FXML private TableColumn<OrdemServico, String> colHistOS;
    @FXML private TableColumn<OrdemServico, String> colHistVeiculo;
    @FXML private TableColumn<OrdemServico, String> colHistStatus;
    @FXML private TableColumn<OrdemServico, String> colHistData;

    @FXML private ComboBox<OrdemServico> cbFinalizarOS;
    @FXML private DatePicker dpFiltroData;

    public void injetarControlador(IGerenciadorOficina gerenciador) {
        this.control = gerenciador;
        atualizarTabelas();
        carregarCombosCadastro();
    }

    @FXML
    public void initialize() {
        colFilaOS.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("OS #" + cellData.getValue().getNumero() + " - " + cellData.getValue().getCliente().getNome()));
        colFilaVeiculo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getVeiculo().getModelo() + " (" + cellData.getValue().getVeiculo().getChassi() + ")"));
        colFilaStatus.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus().toString()));

        colHistOS.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("OS #" + cellData.getValue().getNumero() + " - " + cellData.getValue().getCliente().getNome()));
        colHistVeiculo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getVeiculo().getModelo() + " (" + cellData.getValue().getVeiculo().getChassi() + ")"));
        colHistStatus.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus().toString()));
        colHistData.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDataFechamento() != null ? cellData.getValue().getDataFechamento() : "---"));
        
        cbPecas.setCellFactory(param -> new ListCell<Pecas>() {
            @Override
            protected void updateItem(Pecas item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNome() + " (Estoque: " + item.getQuantidade() + " | R$ " + item.getPreco() + ")");
                }
            }
        });
        cbPecas.setButtonCell(new ListCell<Pecas>() {
            @Override
            protected void updateItem(Pecas item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNome() + " (Estoque: " + item.getQuantidade() + ")");
                }
            }
        });
    }

    private void carregarCombosCadastro() {
        cbCliente.setItems(FXCollections.observableArrayList(RepositorioClientesArray.getInstance().listarTodos()));
        cbVeiculo.setItems(FXCollections.observableArrayList(RepositorioVeiculosArray.getInstance().listarTodos()));
        cbPecas.setItems(FXCollections.observableArrayList(RepositorioPecasArray.getInstance().listarTodas()));
    }

    private void atualizarTabelas() {
        List<OrdemServico> todas = RepositorioOsArray.getInstance().listarTodas();
        
        List<OrdemServico> fila = todas.stream()
            .filter(os -> os.getStatus() == StatusOS.ABERTA || os.getStatus() == StatusOS.PROCESSO_MANUTENCAO)
            .collect(Collectors.toList());
        tbFilaEspera.setItems(FXCollections.observableArrayList(fila));

        List<OrdemServico> historico = todas.stream()
            .filter(os -> os.getStatus() == StatusOS.FINALIZADA || os.getStatus() == StatusOS.PAGA)
            .collect(Collectors.toList());
        tbHistorico.setItems(FXCollections.observableArrayList(historico));

        cbFinalizarOS.setItems(FXCollections.observableArrayList(fila.stream()
            .filter(os -> os.getStatus() == StatusOS.PROCESSO_MANUTENCAO)
            .collect(Collectors.toList())));
    }

    @FXML
    public void abrirOrdemServico() {
        Cliente c = cbCliente.getValue();
        Veiculo v = cbVeiculo.getValue();
        Pecas p = cbPecas.getValue();
        String qtdText = txtQuantidadePeca.getText();
        String descMO = txtDescricaoMaoDeObra.getText();
        String valorMOText = txtValorMaoDeObra.getText();

        if (c == null || v == null || p == null || qtdText.isEmpty() || descMO.isEmpty() || valorMOText.isEmpty()) {
            lblMensagem.setText("Erro: Preencha todos os campos para abrir a OS!");
            lblMensagem.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            int qtd = Integer.parseInt(qtdText);
            double valorMO = Double.parseDouble(valorMOText);

            if (qtd <= 0 || valorMO < 0) {
                lblMensagem.setText("Erro: Valores numéricos inválidos!");
                lblMensagem.setStyle("-fx-text-fill: red;");
                return;
            }

            if (p.getQuantidade() < qtd) {
                lblMensagem.setText("Erro: Estoque insuficiente de " + p.getNome());
                lblMensagem.setStyle("-fx-text-fill: red;");
                return;
            }

            // Busca pelo óleo obrigatório para calcular o feedback do valor ao usuário
            Pecas oleo = null;
            for (Pecas item : RepositorioPecasArray.getInstance().listarTodas()) {
                if (item.getNome().toLowerCase().contains("oleo") || item.getNome().toLowerCase().contains("óleo")) {
                    oleo = item;
                    break;
                }
            }
            if (oleo == null || oleo.getQuantidade() < 1) {
                lblMensagem.setText("Erro: Falta de 'Óleo' (1 Unidade obrigatória) no estoque!");
                lblMensagem.setStyle("-fx-text-fill: red;");
                return;
            }

            boolean sucesso = control.abrirOS(c.getCpf(), v.getChassi(), p.getCodigo(), qtd, descMO, valorMO);
            
            if (sucesso) {
                double totalCalculado = (p.getPreco() * qtd) + oleo.getPreco() + valorMO;
                lblMensagem.setText(String.format("Sucesso! OS criada. Total: R$ %.2f (1 Óleo retirado automaticamente).", totalCalculado));
                lblMensagem.setStyle("-fx-text-fill: green;");
                limparTudo();
                atualizarTabelas();
                carregarCombosCadastro();
            } else {
                lblMensagem.setText("Erro ao salvar OS. Verifique os dados.");
                lblMensagem.setStyle("-fx-text-fill: red;");
            }

        } catch (NumberFormatException e) {
            lblMensagem.setText("Erro: Digite apenas números válidos em quantidade/valor.");
            lblMensagem.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void finalizarTrabalho() {
        OrdemServico selecionada = cbFinalizarOS.getValue();
        if (selecionada != null) {
            boolean sucesso = control.finalizarServico(selecionada.getNumero());
            if (sucesso) {
                lblMensagem.setText("OS #" + selecionada.getNumero() + " finalizada com êxito!");
                lblMensagem.setStyle("-fx-text-fill: green;");
                limparTudo();
                atualizarTabelas();
            }
        } else {
            lblMensagem.setText("Selecione uma OS em andamento!");
            lblMensagem.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void filtrarPorData() {
        LocalDate dataFiltro = dpFiltroData.getValue();
        if (dataFiltro == null) {
            atualizarTabelas();
            return;
        }
        
        DateTimeFormatter formatadorEntrada = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<OrdemServico> todas = RepositorioOsArray.getInstance().listarTodas();
        
        List<OrdemServico> filtradas = todas.stream()
            .filter(os -> os.getStatus() == StatusOS.FINALIZADA || os.getStatus() == StatusOS.PAGA)
            .filter(os -> {
                if (os.getDataFechamento() == null) return false;
                try {
                    String dataApenas = os.getDataFechamento().split(" ")[0];
                    LocalDate dataDoc = LocalDate.parse(dataApenas, formatadorEntrada);
                    return dataDoc.equals(dataFiltro);
                } catch(Exception e) {
                    return false;
                }
            }).collect(Collectors.toList());
            
        tbHistorico.setItems(FXCollections.observableArrayList(filtradas));
    }

    @FXML
    public void botaoVoltar() { 
        limparTudo(); 
        ScreenManager.getInstance().showMenuPrincipal();
    }

    private void limparCamposCadastro() {
        cbCliente.setValue(null);
        cbVeiculo.setValue(null);
        cbPecas.setValue(null);
        txtQuantidadePeca.clear();
        txtDescricaoMaoDeObra.clear();
        txtValorMaoDeObra.clear();
    }

    private void limpiarTudo() {
        limparCamposCadastro();      
        if (dpFiltroData != null) dpFiltroData.setValue(null);
        if (cbFinalizarOS != null) cbFinalizarOS.setValue(null);
    }
}
