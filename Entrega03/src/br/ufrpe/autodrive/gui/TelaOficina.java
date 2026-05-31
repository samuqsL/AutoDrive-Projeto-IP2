package br.ufrpe.autodrive.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import br.ufrpe.autodrive.negocio.IGerenciadorOficina;

public class TelaOficina {
    
    private IGerenciadorOficina control;

    @FXML private TextField txtNumeroOS;
    @FXML private TextField txtData;
    @FXML private TextField txtCpf;
    @FXML private TextField txtChassi;
    @FXML private TextField txtFinalizarOS;
    @FXML private Label lblMensagem;

    public TelaOficina() {}

    public void injetarGerenciador(IGerenciadorOficina control) {
        this.control = control;
    }

    @FXML
    private void botaoAbrirOS() {
        try {
            int numero = Integer.parseInt(txtNumeroOS.getText().trim());
            String data = txtData.getText().trim();
            String cpf = txtCpf.getText().trim();
            String chassi = txtChassi.getText().trim();

            if (data.isEmpty() || cpf.isEmpty() || chassi.isEmpty()) {
                lblMensagem.setText("X Erro: Todos os campos são obrigatórios.");
                lblMensagem.setStyle("-fx-text-fill: red;");
                return;
            }

            if (control.abrirOS(numero, data, cpf, chassi)) {
                lblMensagem.setText("✓ Sucesso: OS " + numero + " aberta e Veículo em manutenção.");
                lblMensagem.setStyle("-fx-text-fill: green;");
                limparCamposCadastro();
            } else {
                lblMensagem.setText("X Erro: Dados inválidos ou OS já existente.");
                lblMensagem.setStyle("-fx-text-fill: red;");
            }
        } catch (NumberFormatException e) {
            lblMensagem.setText("X Erro: O número da OS deve ser um valor numérico inteiro.");
            lblMensagem.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void botaoFinalizarOS() {
        try {
            int numero = Integer.parseInt(txtFinalizarOS.getText().trim());

            if (control.finalizarServico(numero)) {
                lblMensagem.setText("✓ Sucesso: OS " + numero + " finalizada e Veículo disponível.");
                lblMensagem.setStyle("-fx-text-fill: green;");
                txtFinalizarOS.clear();
            } else {
                lblMensagem.setText("X Erro: OS não paga ou falta óleo na revisão.");
                lblMensagem.setStyle("-fx-text-fill: red;");
            }
        } catch (NumberFormatException e) {
            lblMensagem.setText("X Erro: Digite um número de OS válido.");
            lblMensagem.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void botaoVoltar() {
        lblMensagem.setText("Pronto para operar");
        lblMensagem.setStyle("-fx-text-fill: #7f8c8d;");
        ScreenManager.getInstance().showMenuPrincipal();
    }

    private void limparCamposCadastro() {
        txtNumeroOS.clear();
        txtData.clear();
        txtCpf.clear();
        txtChassi.clear();
    }
}
