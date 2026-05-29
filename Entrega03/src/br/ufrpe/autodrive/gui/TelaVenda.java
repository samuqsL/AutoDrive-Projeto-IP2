package br.ufrpe.autodrive.gui;

import java.util.List;

import br.ufrpe.autodrive.negocio.IGerenciadorVenda;
import br.ufrpe.autodrive.negocio.beans.Notificacao;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class TelaVenda {

    // =========================================================================
    // 🟢 1. Componentes Vinculados ao FXML (Scene Builder)
    // =========================================================================
    
    // Gerenciadores de Layout (Painéis Invisíveis/Visíveis)
    @FXML private VBox painelMenuVendas;
    @FXML private VBox painelFormulario;

    // Campos de Entrada de Dados (Inputs)
    @FXML private TextField txtNumero;
    @FXML private TextField txtCpf;
    @FXML private TextField txtChassi;
    @FXML private TextField txtVendedor;
    @FXML private TextField txtEntrada;

    // Componentes de Saída de Dados (Feedbacks para o usuário)
    @FXML private Label lblStatus;
    @FXML private TextArea txtAreaAlertas; // Um campo de texto grande para listar os alertas na interface

    // Atributo de negócio (Injetado)
    private IGerenciadorVenda control; 

    // Construtor Padrão Vazio necessário para o JavaFX
    public TelaVenda() {}

    /**
     * Método para Injetar o Gerenciador vindo das camadas internas.
     */
    public void injetarGerenciador(IGerenciadorVenda gVenda) {
        this.control = gVenda;
        System.out.println("-> [TelaVenda] Gerenciador de Vendas injetado com sucesso!");
    }

    // =========================================================================
    // 🟢 2. Métodos de Chaveamento de Telas (Ações da Interface)
    // =========================================================================

    /**
     * Chamado pelo botão "Realizar Nova Venda" no menu de vendas.
     * Esconde as opções principais e exibe o formulário de inputs.
     */
    @FXML
    public void acaoAbrirFormulario() {
        limparCamposFormulario();
        
        // Troca de visual alternando visibilidade e gerenciamento de espaço
        painelMenuVendas.setVisible(false);
        painelMenuVendas.setManaged(false);
        
        painelFormulario.setVisible(true);
        painelFormulario.setManaged(true);
    }

    /**
     * Chamado pelo botão "Voltar" dentro do formulário ou após concluir a operação.
     * Esconde o formulário e traz o usuário de volta ao menu anterior de vendas.
     */
    @FXML
    public void acaoVoltarMenuVendas() {
        painelFormulario.setVisible(false);
        painelFormulario.setManaged(false);
        
        painelMenuVendas.setVisible(true);
        painelMenuVendas.setManaged(true);
    }

    // =========================================================================
    // 🟢 3. Operações de Negócio (Substitutos do Scanner)
    // =========================================================================

    /**
     * Chamado pelo botão "Confirmar Venda" dentro do formulário.
     * Captura os textos das caixas, valida e executa no repositório.
     */
    @FXML
    public void botaoConfirmarVenda() {
        System.out.println("\n--- [GUI] EXECUTANDO EFETUAR NOVA VENDA ---");
        lblStatus.setText(""); // Reseta mensagem anterior

        try {
            // 1. Captura os dados dos TextFields substituindo o antigo Scanner!
            int numero = Integer.parseInt(txtNumero.getText().trim());
            String cpfCliente = txtCpf.getText().trim();
            String chassi = txtChassi.getText().trim();
            String nomeVendedor = txtVendedor.getText().trim();
            double entrada = Double.parseDouble(txtEntrada.getText().trim());

            // 2. Validação simples de campos vazios antes de enviar ao backend
            if (cpfCliente.isEmpty() || chassi.isEmpty() || nomeVendedor.isEmpty()) {
                lblStatus.setText("❌ ERRO: Preencha todos os campos obrigatórios.");
                lblStatus.setStyle("-fx-text-fill: red;");
                return;
            }

            // 3. Comunicação direta com a regra de negócio igual à entrega 02
            boolean sucesso = control.efetuarVenda(numero, cpfCliente, chassi, nomeVendedor, entrada);

            if (sucesso) {
                lblStatus.setText("✅ SUCESSO: Venda registrada na base de dados!");
                lblStatus.setStyle("-fx-text-fill: green;");
                
                // Opcional: Você pode chamar acaoVoltarMenuVendas() aqui caso queira fechar o form na hora
            } else {
                lblStatus.setText("❌ ERRO: Cadastro inexistente (Cliente/Vendedor/Veículo) ou veículo indisponível.");
                lblStatus.setStyle("-fx-text-fill: red;");
            }

        } catch (NumberFormatException e) {
            lblStatus.setText("⚠️ ERRO: Use apenas números inteiros em 'Número' e decimais em 'Entrada'.");
            lblStatus.setStyle("-fx-text-fill: orange;");
        }
    }

    /**
     * Chamado pelo botão "Verificar Alertas" no menu de vendas.
     * Busca as notificações e imprime no TextArea da janela gráfica.
     */
    @FXML
    public void botaoVerificarAlertas() {
        System.out.println("\n--- [GUI] BUSCANDO ALERTAS DE REVISÃO ---");
        txtAreaAlertas.clear(); // Limpa resultados de buscas anteriores
        
        List<Notificacao> alertas = control.listarAlertasRevisao(); 
    
        if (alertas == null || alertas.isEmpty()) {
            txtAreaAlertas.setText("Nenhum veículo precisa de revisão no momento.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("--- VEÍCULOS REQUERENDO REVISÃO TRATADOS ---\n\n");
            
            for (Notificacao n : alertas) {
                sb.append("[ALERTA] Cliente: ").append(n.getCliente().getNome())
                  .append(" | Veículo: ").append(n.getVeiculo().getModelo())
                  .append(" | KM Atual: ").append(n.getQuilometragem())
                  .append("\n----------------------------------------\n");
            }
            
            // Exibe a listagem diretamente na caixa de texto na tela do usuário
            txtAreaAlertas.setText(sb.toString());
        }
    }
    
    @FXML
    public void acaoSairParaMenuPrincipal() {
        System.out.println("-> [TelaVenda] Voltando para o Menu Principal...");
        
        // Altera a cena do palco principal de volta para o MenuPrincipal que está salvo na memória
        ScreenManager.getInstance().showMenuPrincipal();
    }
    
    /**
     * Método auxiliar de limpeza de interface
     */
    private void limparCamposFormulario() {
        txtNumero.clear();
        txtCpf.clear();
        txtChassi.clear();
        txtVendedor.clear();
        txtEntrada.clear();
        lblStatus.setText("");
    }
}
