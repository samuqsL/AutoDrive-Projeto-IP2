package br.ufrpe.autodrive.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import br.ufrpe.autodrive.negocio.IGerenciadorOficina;

public class TelaOficina {
    
    private IGerenciadorOficina control;

    @FXML private TextField txtCpf;
    @FXML private TextField txtChassi;
    @FXML private TextField txtFinalizarOS;

    public void injetarGerenciador(IGerenciadorOficina control) {
        this.control = control;
    }

    @FXML
    public void botaoAbrirOS() { 
        try {
            String cpf = txtCpf.getText().trim();
            String chassi = txtChassi.getText().trim();

            if (!cpf.isEmpty() && !chassi.isEmpty()) {
                int numeroGerado = control.abrirOS(cpf, chassi);
                if (numeroGerado != -1) {
                    limparCamposCadastro();
                }
            }
        } catch (Exception e) {
            // Execução silenciosa
        }
    }

    @FXML
    public void botaoFinalizarOS() { 
        try {
            String txtFinalizar = txtFinalizarOS.getText().trim();
            if (!txtFinalizar.isEmpty()) {
                int numero = Integer.parseInt(txtFinalizar);
                if (control != null && control.finalizarServico(numero)) {
                    txtFinalizarOS.clear();
                }
            }
        } catch (NumberFormatException e) {
            // Execução silenciosa
        }
    }

    @FXML
    public void botaoVoltar() { 
        limparTudo(); 
        ScreenManager.getInstance().showMenuPrincipal();
    }

    private void limparCamposCadastro() {
        if (txtCpf != null) txtCpf.clear();
        if (txtChassi != null) txtChassi.clear();
    }

    private void limparTudo() {
        limparCamposCadastro();      
        if (txtFinalizarOS != null) txtFinalizarOS.clear();      
    }
}
