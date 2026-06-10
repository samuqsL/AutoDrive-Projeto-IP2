package br.ufrpe.autodrive.gui;

import br.ufrpe.autodrive.negocio.IGerenciadorTestDrive;
import br.ufrpe.autodrive.negocio.beans.TestDrive;
import br.ufrpe.autodrive.negocio.beans.Cliente;
import br.ufrpe.autodrive.negocio.beans.Veiculo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

public class TelaTestDrive {

    // --- Lado Esquerdo (Agendamento) ---
    @FXML private ComboBox<Cliente> comboCliente;
    @FXML private ComboBox<Veiculo> comboVeiculo;
    @FXML private DatePicker datePickerData;
    @FXML private TextField txtHora;
    @FXML private Label lblMensagem;

    // --- centro (Histórico e Filtros) ---
    @FXML private DatePicker dpFiltroInicio;
    @FXML private DatePicker dpFiltroFim;
    @FXML private TableView<TestDrive> tabelaTestDrives; // renomear no FXML para tabelaTestDrives depois!
    @FXML private TableColumn<TestDrive, String> colCPF;
    @FXML private TableColumn<TestDrive, String> colNome;
    @FXML private TableColumn<TestDrive, String> colChassi;
    @FXML private TableColumn<TestDrive, String> colData;
    @FXML private TableColumn<TestDrive, String> colHora;
    
    // --- Lado direito (cancelamento) ---
    @FXML private TextField txtIDCancelamento;
    @FXML private TableColumn<TestDrive, String> colID;

    private IGerenciadorTestDrive control;
    private ObservableList<TestDrive> obsTestDrives; // Lista especial que o JavaFX consegue "observar"

    public void injetarGerenciador(IGerenciadorTestDrive gT) {
        this.control = gT;
        // Quando o gerenciador chegar, a gente já configura e carrega a tabela!
        configurarTabela();
        carregarTabelaCompleta();
        
        if (control != null) {
            comboCliente.setItems(FXCollections.observableArrayList(control.listarTodosClientes()));
            comboVeiculo.setItems(FXCollections.observableArrayList(control.listarTodosVeiculos()));
        }
    }

    // ==========================================
    // LÓGICA DA TABELA E FILTROS
    // ==========================================

    private void configurarTabela() {
    	// Ensina a coluna do ID a ler o atributo "id"
    	colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCPF.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCliente().getCpf()));
        colChassi.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVeiculo().getChassi()));
            
        // Formatando a data para o padrão Brasileiro (dd/MM/yyyy)
        DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colData.setCellValueFactory(cellData -> {
            LocalDate data = cellData.getValue().getDataTestDrive().toLocalDate();
            return new SimpleStringProperty(data.format(formatadorData));
        });

        // Extraindo apenas a hora
        colHora.setCellValueFactory(cellData -> {
            LocalTime hora = cellData.getValue().getDataTestDrive().toLocalTime();
            return new SimpleStringProperty(hora.toString());
        });
        
        colNome.setCellValueFactory(cellData -> {
            // Verifica se o agendamento e o cliente não são nulos para evitar NullPointerException
            if (cellData.getValue() != null && cellData.getValue().getCliente() != null) {
                return new SimpleStringProperty(cellData.getValue().getCliente().getNome());
            }
            return new SimpleStringProperty("");
        });
    }

    private void carregarTabelaCompleta() {
        if (control != null) {
            List<TestDrive> todos = control.listarTestDrives();
            // Transforma a lista normal do Java numa lista que a TableView entende
            obsTestDrives = FXCollections.observableArrayList(todos);
            tabelaTestDrives.setItems(obsTestDrives);
        }
    }

    @FXML
    public void acaoFiltrarPorPeriodo() {
        LocalDate dataInicio = dpFiltroInicio.getValue();
        LocalDate dataFim = dpFiltroFim.getValue();

        if (dataInicio == null || dataFim == null) {
            lblMensagem.setTextFill(Color.RED);
            lblMensagem.setText("Selecione a data de Início e Fim para filtrar.");
            return;
        }

        List<TestDrive> todos = control.listarTestDrives();
        
        // Filtra a lista usando Streams do Java baseado no período escolhido
        List<TestDrive> filtrados = todos.stream()
            .filter(td -> {
                LocalDate dataTd = td.getDataTestDrive().toLocalDate();
                // Verifica se a data do test drive está entre o início e o fim (inclusive)
                return (dataTd.isEqual(dataInicio) || dataTd.isAfter(dataInicio)) &&
                       (dataTd.isEqual(dataFim) || dataTd.isBefore(dataFim));
            })
            .collect(Collectors.toList());

        // Atualiza a tabela só com os filtrados
        obsTestDrives = FXCollections.observableArrayList(filtrados);
        tabelaTestDrives.setItems(obsTestDrives);
        
        lblMensagem.setTextFill(Color.GREEN);
        lblMensagem.setText("Filtro aplicado com sucesso!");
    }

    @FXML
    public void acaoLimparFiltro() {
        dpFiltroInicio.setValue(null);
        dpFiltroFim.setValue(null);
        lblMensagem.setText("");
        carregarTabelaCompleta(); // Volta a mostrar todos
    }

    // ==========================================
    // LÓGICA DE AGENDAMENTO
    // ==========================================

    @FXML
    public void tratarBotaoAgendar() {
    	Cliente c = comboCliente.getValue();
        Veiculo v = comboVeiculo.getValue();
        LocalDate dataEscolhida = datePickerData.getValue();
        String horaDigitada = txtHora.getText() != null ? txtHora.getText().trim() : "";

        lblMensagem.setText("");

        if (c == null || v == null || dataEscolhida == null || horaDigitada.isEmpty()) {
            lblMensagem.setTextFill(Color.RED);
            lblMensagem.setText("Por favor, preencha todos os campos!");
            return;
        }

        LocalDateTime dataHoraFinal;
        try {
            LocalTime hora = LocalTime.parse(horaDigitada); 
            dataHoraFinal = LocalDateTime.of(dataEscolhida, hora); 
        } catch (DateTimeParseException e) {
            lblMensagem.setTextFill(Color.RED);
            lblMensagem.setText("Erro: Digite a hora no formato HH:mm (Ex: 14:30)");
            return; 
        }

     // Passa o CPF e o Chassi usando os objetos selecionados
        boolean sucesso = control.agendarTestDrive(c.getCpf(), v.getChassi(), dataHoraFinal);
        if (sucesso) {
            lblMensagem.setTextFill(Color.GREEN);
            lblMensagem.setText(">>> SUCESSO: Agendamento realizado!");
            limparCampos();
            carregarTabelaCompleta(); 
        } else {
            lblMensagem.setTextFill(Color.RED);
            lblMensagem.setText(">>> ERRO: Conflito, ou carro indisponível.");
        }
    }
    
   // acao de cancelar pelo ID (hash)
    @FXML
    public void acaoCancelarAgendamento() {
        String id = txtIDCancelamento.getText() != null ? txtIDCancelamento.getText().trim() : "";
        
        if (id.isEmpty()) {
            lblMensagem.setTextFill(Color.RED);
            lblMensagem.setText("Digite o ID para cancelar!");
            return;
        }

        boolean cancelou = control.cancelarTestDrive(id);

        if (cancelou) {
            lblMensagem.setTextFill(Color.GREEN);
            lblMensagem.setText("Agendamento " + id + " cancelado com sucesso!");
            txtIDCancelamento.clear();
            carregarTabelaCompleta(); // Atualiza a tabela pra sumir o cancelado
        } else {
            lblMensagem.setTextFill(Color.RED);
            lblMensagem.setText("ID não encontrado. Verifique na tabela.");
        }
    }

    @FXML
    public void tratarBotaoVoltar() {
        limparCampos();
        acaoLimparFiltro();
        lblMensagem.setText("");
        ScreenManager.getInstance().showMenuPrincipal();
    }

    private void limparCampos() {
        comboCliente.setValue(null);
        comboVeiculo.setValue(null);
        datePickerData.setValue(null);
        txtHora.clear();
    }
}
