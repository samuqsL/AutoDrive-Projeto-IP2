package br.ufrpe.autodrive.gui;

import br.ufrpe.autodrive.negocio.IGerenciadorOficina;
import br.ufrpe.autodrive.negocio.IGerenciadorRelatorio;
import br.ufrpe.autodrive.negocio.IGerenciadorTestDrive;
import br.ufrpe.autodrive.negocio.IGerenciadorVenda;
import br.ufrpe.autodrive.negocio.IGerenciadorCadastro; 
import br.ufrpe.autodrive.negocio.IGerenciadorEstoquePecas; // 💡 NOVO: Importação adicionada
import javafx.fxml.FXML;

public class MenuPrincipal {

    // 1. ATRIBUTOS
    private IGerenciadorVenda gVenda;
    private IGerenciadorOficina gOficina;
    private IGerenciadorRelatorio gRelatorio;
    private IGerenciadorTestDrive gTestDrive;
    private IGerenciadorCadastro gCadastro; 
    private IGerenciadorEstoquePecas gEstoquePecas; // 💡 NOVO: Atributo adicionado!

    // 2. CONSTRUTOR PADRÃO VAZIO
    public MenuPrincipal() {
    }

    // 3. MÉTODO DE INJEÇÃO ATUALIZADO
    public void injetarGerenciadores(IGerenciadorVenda gV, IGerenciadorOficina gO, 
                                     IGerenciadorRelatorio gR, IGerenciadorTestDrive gT, 
                                     IGerenciadorCadastro gC, IGerenciadorEstoquePecas gEstoque) { // 💡 Adicionado
        this.gVenda = gV;
        this.gOficina = gO;
        this.gRelatorio = gR;
        this.gTestDrive = gT;
        this.gCadastro = gC;
        this.gEstoquePecas = gEstoque; // 💡 Vincula localmente o estoque
        System.out.println("-> [MenuPrincipal] Gerenciadores de negócio (incluindo Estoque) injetados com sucesso!");
    }

    // 4. MÉTODOS DOS BOTÕES (@FXML)

    @FXML
    public void tratarBotaoVenda() {
        System.out.println("-> [MenuPrincipal] Botão Tela de Venda clicado!");
        ScreenManager.getInstance().showTelaVenda();
    }

    @FXML
    public void tratarBotaoOficina() {
        System.out.println("-> [MenuPrincipal] Botão Tela da Oficina clicado!");
        ScreenManager.getInstance().showTelaOficina();
    }

    @FXML
    public void tratarBotaoTestDrive() {
        System.out.println("-> [MenuPrincipal] Botão Tela TestDrive clicado!");
        ScreenManager.getInstance().showTelaTestDrive();
    }

    @FXML
    public void tratarBotaoRelatorio() {
        System.out.println("-> [MenuPrincipal] Botão Tela de Relatórios clicado!");
        ScreenManager.getInstance().showTelaRelatorio();
    }
    
    @FXML
    public void tratarBotaoCadastro() {
        System.out.println("-> [MenuPrincipal] Botão Tela de Cadastros Gerais clicado!");
        ScreenManager.getInstance().showTelaCadastro(); 
    }
    
    // 💡 NOVO: Método disparado pelo botão de Estoque no FXML do MenuPrincipal
    @FXML
    public void tratarBotaoEstoque() {
        System.out.println("-> [MenuPrincipal] Botão Tela de Estoque de Peças clicado!");
        ScreenManager.getInstance().showTelaEstoquePecas();
    }
    
    @FXML
    public void tratarBotaoSair() {
        System.out.println("-> [MenuPrincipal] Fechando o sistema AutoDrive com segurança...");
        System.exit(0); 
    }
}
