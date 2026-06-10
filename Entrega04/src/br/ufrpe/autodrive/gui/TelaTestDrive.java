package br.ufrpe.autodrive.gui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import br.ufrpe.autodrive.negocio.IGerenciadorTestDrive;
import br.ufrpe.autodrive.negocio.beans.Cliente;
import br.ufrpe.autodrive.negocio.beans.TestDrive;
import br.ufrpe.autodrive.negocio.beans.Veiculo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class TelaTestDrive {

    // --- Lado Esquerdo (Agendamento) ---
    @FXML private ComboBox<Cliente> comboCliente;
    @FXML private ComboBox<Veiculo> comboVeiculo;
    @FXML private DatePicker datePickerData;
    @FXML private TextField txtHora;
    @FXML private Label lblMensagem;

    // --- Centro (Histórico e Filtros) ---
    @FXML private DatePicker dpFiltroInicio;
    @FXML private DatePicker dpFiltroFim;
    @FXML private TableView<TestDrive> tabelaTestDrives; 
    @FXML private TableColumn<TestDrive, String> colCPF;
    @FXML private TableColumn<TestDrive, String> colNome;
    @FXML private TableColumn<TestDrive, String> colChassi;
    @FXML private TableColumn<TestDrive, String> colData;
    @FXML private TableColumn<TestDrive, String> colHora;
    @FXML private TableColumn<TestDrive, String> colID;
    
    // --- Lado Direito (Cancelamento) ---
    @FXML private TextField txtIDCancelamento;

    private IGerenciadorTestDrive control;
    private ObservableList<TestDrive> obsTestDrives;

    public void injetarGerenciador(IGerenciadorTestDrive gT) {
        this.control = gT;
        configurarTabela();
        carregarTabelaCompleta();
        
        // 🟢 CORREÇÃO CRÍTICA: Mapeamento de itens movido para dentro da checagem 'if'
        // Evita que o sistema lance um NullPointerException caso o gerenciador gT falhe ao carregar.
        if (this.control != null) {
            List<Cliente> clientes = control.listarTodosClientes();
            List<Veiculo> veiculos = control.listarTodosVeiculos();
            
            if (clientes != null) {
                comboCliente.setItems(FXCollections.observableArrayList(clientes));
            }
            if (veiculos != null) {
                comboVeiculo.setItems(FXCollections.observableArrayList(veiculos));
            }
        }
    }

    private void configurarTabela() {
        // Suas validações preventivas aqui estão perfeitas! Funcionam melhor do que try-catch.
        colID.setCellValueFactory(cellData -> {
            if (cellData.getValue() != null && cellData.getValue().getId() != null) {
                return new SimpleStringProperty(cellData.getValue().getId());
            }
            return new SimpleStringProperty("");
        });

        colCPF.setCellValueFactory(cellData -> {
            if (cellData.getValue() != null && cellData.getValue().getCliente() != null) {
                return new SimpleStringProperty(cellData.getValue().getCliente().getCpf());
            }
            return new SimpleStringProperty("");
        });

        colChassi.setCellValueFactory(cellData -> {
            if (cellData.getValue() != null && cellData.getValue().getVeiculo() != null) {
                return new SimpleStringProperty(cellData.getValue().getVeiculo().getChassi());
            }
            return new SimpleStringProperty("");
        });
            
        DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colData.setCellValueFactory(cellData -> {
            if (cellData.getValue() != null && cellData.getValue().getDataTestDrive() != null) {
                LocalDate data = cellData.getValue().getDataTestDrive().toLocalDate();
                return new SimpleStringProperty(data.format(formatadorData));
            }
            return new SimpleStringProperty("");
        });

        colHora.setCellValueFactory(cellData -> {
            if (cellData.getValue() != null && cellData.getValue().getDataTestDrive() != null) {
                LocalTime hora = cellData.getValue().getDataTestDrive().toLocalTime();
                return new SimpleStringProperty(hora.toString());
            }
            return new SimpleStringProperty("");
        });
        
        colNome.setCellValueFactory(cellData -> {
            if (cellData.getValue() != null && cellData.getValue().getCliente() != null) {
                return new SimpleStringProperty(cellData.getValue().getCliente().getNome());
            }
            return new SimpleStringProperty("");
        });
    }

    private void carregarTabelaCompleta() {
        if (control != null) {
            List<TestDrive> todos = control.listarTestDrives();
            if (todos != null) {
                obsTestDrives = FXCollections.observableArrayList(todos);
                tabelaTestDrives.setItems(obsTestDrives);
            }
        }
    }

    @FXML
    public void acaoFiltrarPorPeriodo() {
        if (control == null) return;
        
        LocalDate dataInicio = dpFiltroInicio.getValue();
        LocalDate dataFim = dpFiltroFim.getValue();

        if (dataInicio == null || dataFim == null) {
            lblMensagem.setTextFill(Color.RED);
            lblMensagem.setText("Selecione a data de Início e Fim para filtrar.");
            return;
        }

        if (dataInicio.isAfter(dataFim)) {
            lblMensagem.setTextFill(Color.RED);
            lblMensagem.setText("A data de início não pode ser posterior à data fim.");
            return;
        }

        List<TestDrive> todos = control.listarTestDrives();
        if (todos == null) return; // 🟢 Proteção contra retorno nulo do repositório

        List<TestDrive> filtrados = todos.stream()
            .filter(td -> {
                if (td == null || td.getDataTestDrive() == null) return false;
                LocalDate dataTd = td.getDataTestDrive().toLocalDate();
                return (dataTd.isEqual(dataInicio) || dataTd.isAfter(dataInicio)) &&
                       (dataTd.isEqual(dataFim) || dataTd.isBefore(dataFim));
            })
            .collect(Collectors.toList());

        obsTestDrives = FXCollections.observableArrayList(filtrados);
        tabelaTestDrives.setItems(obsTestDrives);
        
        lblMensagem.setTextFill(Color.BLUE);
        lblMensagem.setText("Filtro de período aplicado!");
    }

    @FXML
    public void acaoLimparFiltro() {
        dpFiltroInicio.setValue(null);
        dpFiltroFim.setValue(null);
        if (lblMensagem != null) lblMensagem.setText("");
        carregarTabelaCompleta();
    }

    @FXML
    public void tratarBotaoAgendar() {
        Cliente c = comboCliente.getValue();
        Veiculo v = comboVeiculo.getValue();
        LocalDate dataEscolhida = datePickerData.getValue();
        String horaDigitada = txtHora.getText() != null ? txtHora.getText().trim() : "";

        if (lblMensagem != null) lblMensagem.setText("");

        if (c == null || v == null || dataEscolhida == null || horaDigitada.isEmpty()) {
            if (lblMensagem != null) {
                lblMensagem.setTextFill(Color.RED);
                lblMensagem.setText("Por favor, preencha todos os campos!");
            }
            return;
        }

        LocalDateTime dataHoraFinal;
        try {
            LocalTime hora = LocalTime.parse(horaDigitada); 
            dataHoraFinal = LocalDateTime.of(dataEscolhida, hora); 
        } catch (DateTimeParseException e) {
            if (lblMensagem != null) {
                lblMensagem.setTextFill(Color.RED);
                lblMensagem.setText("Erro: Use o formato HH:mm (Ex: 14:30)");
            }
            return; 
        }

        if (control == null) return; // 🟢 Proteção caso o botão seja clicado sem controller injetado

        boolean sucesso = control.agendarTestDrive(c.getCpf(), v.getChassi(), dataHoraFinal);
        if (sucesso) {
            if (lblMensagem != null) {
                lblMensagem.setTextFill(Color.GREEN);
                lblMensagem.setText("Sucesso: Agendamento realizado!");
            }
            limparCampos();
            carregarTabelaCompleta(); 
        } else {
            if (lblMensagem != null) {
                lblMensagem.setTextFill(Color.RED);
                lblMensagem.setText("Erro: Conflito de agenda ou carro ocupado.");
            }
        }
    }
    
    @FXML
    public void acaoCancelarAgendamento() {
        String id = txtIDCancelamento.getText() != null ? txtIDCancelamento.getText().trim() : "";
        
        if (id.isEmpty()) {
            if (lblMensagem != null) {
                lblMensagem.setTextFill(Color.RED);
                lblMensagem.setText("Digite o ID para cancelar!");
            }
            return;
        }

        if (control == null) return;

        boolean cancelou = control.cancelarTestDrive(id);

        if (cancelou) {
            if (lblMensagem != null) {
                lblMensagem.setTextFill(Color.GREEN);
                lblMensagem.setText("Agendamento " + id + " cancelado com sucesso!");
            }
            txtIDCancelamento.clear();
            carregarTabelaCompleta();
        } else {
            if (lblMensagem != null) {
                lblMensagem.setTextFill(Color.RED);
                lblMensagem.setText("ID não encontrado. Verifique na tabela.");
            }
        }
    }

    @FXML
    public void tratarBotaoVoltar() {
        limparCampos();
        acaoLimparFiltro();
        if (lblMensagem != null) lblMensagem.setText("");
        ScreenManager.getInstance().showMenuPrincipal();
    }

    private void limparCampos() {
        comboCliente.setValue(null);
        comboVeiculo.setValue(null);
        datePickerData.setValue(null);
        txtHora.clear();
    }
}