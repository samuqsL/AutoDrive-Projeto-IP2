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
    public void botaoAbrirOS() { 
        try {
            if (txtNumeroOS.getText().trim().isEmpty()) {
                lblMensagem.setText("X Erro: O número da OS é obrigatório.");
                lblMensagem.setStyle("-fx-text-fill: red;");
                return;
            }
            
            int numero = Integer.parseInt(txtNumeroOS.getText().trim());
            String data = txtData.getText().trim();
            String cpf = txtCpf.getText().trim();
            String chassi = txtChassi.getText().trim();

            if (data.isEmpty() || cpf.isEmpty() || chassi.isEmpty()) {
                lblMensagem.setText("X Erro: Todos os campos são obrigatórios.");
                lblMensagem.setStyle("-fx-text-fill: red;");
                return;
            }

            if (control != null && control.abrirOS(numero, data, cpf, chassi)) {
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
    public void botaoFinalizarOS() { 
        try {
            if (txtFinalizarOS.getText().trim().isEmpty()) {
                lblMensagem.setText("X Erro: Digite o número da OS para finalizar.");
                lblMensagem.setStyle("-fx-text-fill: red;");
                return;
            }

            int numero = Integer.parseInt(txtFinalizarOS.getText().trim());

            if (control != null && control.finalizarServico(numero)) {
                lblMensagem.setText("✓ Sucesso: OS " + numero + " finalizada e Veículo disponível.");
                lblMensagem.setStyle("-fx-text-fill: green;");
                txtFinalizarOS.clear();
            } else {
                lblMensagem.setText("X Erro: OS não encontrada ou requisitos não preenchidos.");
                lblMensagem.setStyle("-fx-text-fill: red;");
            }
        } catch (NumberFormatException e) {
            lblMensagem.setText("X Erro: Digite um número de OS válido.");
            lblMensagem.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void botaoVoltar() { 
        // 🟢 CORREÇÃO: Limpa todos os campos e labels ANTES de ir para o menu
        limparTudo(); 
        ScreenManager.getInstance().showMenuPrincipal();
    }

    private void limparCamposCadastro() {
        txtNumeroOS.clear();
        txtData.clear();
        txtCpf.clear();
        txtChassi.clear();
    }

    // 🟢 NOVO MÉTODO: Faz uma limpeza completa em todos os quadrantes da tela
    private void limparTudo() {
        limparCamposCadastro();      // Limpa os 4 campos da esquerda
        txtFinalizarOS.clear();      // Limpa o campo da direita
        lblMensagem.setText("Pronto para operar"); // Reseta o texto padrão
        lblMensagem.setStyle("-fx-text-fill: #7f8c8d;"); // Reseta a cor cinza estável
    }
}
