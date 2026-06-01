package br.ufrpe.autodrive.gui;

import br.ufrpe.autodrive.negocio.IGerenciadorTestDrive;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class TelaTestDrive {

    @FXML private TextField txtCpf;
    @FXML private TextField txtChassi;
    
    @FXML private DatePicker datePickerData;
    @FXML private TextField txtHora;
    
    @FXML private Label lblMensagem;

    private IGerenciadorTestDrive control;

    public void injetarGerenciador(IGerenciadorTestDrive gT) {
        this.control = gT;
    }

    @FXML
    public void tratarBotaoAgendar() {
        String cpf = txtCpf.getText();
        String chassi = txtChassi.getText();
        LocalDate dataEscolhida = datePickerData.getValue();
        String horaDigitada = txtHora.getText();

        lblMensagem.setText("");

        // 1. Valida se a pessoa não deixou nada em branco
        if (cpf.isEmpty() || chassi.isEmpty() || dataEscolhida == null || horaDigitada.isEmpty()) {
            lblMensagem.setTextFill(Color.RED);
            lblMensagem.setText("Por favor, preencha todos os campos!");
            return;
        }

        LocalDateTime dataHoraFinal;

        // 2. Tenta converter a hora digitada para o formato certo
        try {
            LocalTime hora = LocalTime.parse(horaDigitada); // Tenta ler o "14:30"
            dataHoraFinal = LocalDateTime.of(dataEscolhida, hora); // Junta o dia com a hora
        } catch (DateTimeParseException e) {
            lblMensagem.setTextFill(Color.RED);
            lblMensagem.setText("Erro: Digite a hora no formato HH:mm (Ex: 14:30)");
            return; // Interrompe para a pessoa corrigir
        }

        // 3. Chama o NOVO método do Gerenciador, passando a data e hora!
        boolean sucesso = control.agendarTestDrive(cpf, chassi, dataHoraFinal);

        if (sucesso) {
            lblMensagem.setTextFill(Color.GREEN);
            lblMensagem.setText(">>> SUCESSO: Agendamento realizado!");
            
            // Limpa tudo pra deixar pronto pro próximo agendamento
            txtCpf.clear();
            txtChassi.clear();
            datePickerData.setValue(null);
            txtHora.clear();
        } else {
            lblMensagem.setTextFill(Color.RED);
            lblMensagem.setText(">>> ERRO: Cliente, Chassi ou CNH inválidos.");
        }
    }

    @FXML
    public void tratarBotaoVoltar() {
        txtCpf.clear();
        txtChassi.clear();
        datePickerData.setValue(null);
        txtHora.clear();
        lblMensagem.setText("");
        
        ScreenManager.getInstance().showMenuPrincipal();
    }
}
