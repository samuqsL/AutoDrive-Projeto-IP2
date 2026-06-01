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
        // 🟢 Correção: Removendo espaços em branco com trim() nas strings de entrada
        String cpf = txtCpf.getText() != null ? txtCpf.getText().trim() : "";
        String chassi = txtChassi.getText() != null ? txtChassi.getText().trim() : "";
        LocalDate dataEscolhida = datePickerData.getValue();
        String horaDigitada = txtHora.getText() != null ? txtHora.getText().trim() : "";

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
            return; 
        }

        // 3. Chama o método do Gerenciador passando a data customizada
        boolean sucesso = control.agendarTestDrive(cpf, chassi, dataHoraFinal);

        if (sucesso) {
            lblMensagem.setTextFill(Color.GREEN);
            lblMensagem.setText(">>> SUCESSO: Agendamento realizado!");
            limparCampos();
        } else {
            lblMensagem.setTextFill(Color.RED);
            lblMensagem.setText(">>> ERRO: Cliente, Chassi ou CNH inválidos, ou carro em manutenção.");
        }
    }

    @FXML
    public void tratarBotaoVoltar() {
        limparCampos();
        lblMensagem.setText("");
        ScreenManager.getInstance().showMenuPrincipal();
    }

    // Método auxiliar para reaproveitar a limpeza de campos
    private void limparCampos() {
        txtCpf.clear();
        txtChassi.clear();
        datePickerData.setValue(null);
        txtHora.clear();
    }
}
