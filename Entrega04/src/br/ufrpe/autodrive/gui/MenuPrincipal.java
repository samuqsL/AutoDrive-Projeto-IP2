package br.ufrpe.autodrive.gui;

// Importa as Interfaces do seu Negócio
import br.ufrpe.autodrive.negocio.IGerenciadorOficina;
import br.ufrpe.autodrive.negocio.IGerenciadorRelatorio;
import br.ufrpe.autodrive.negocio.IGerenciadorTestDrive;
import br.ufrpe.autodrive.negocio.IGerenciadorVenda;
import javafx.fxml.FXML;

public class MenuPrincipal {

    // 1. ATRIBUTOS: Mantemos os gerenciadores que guardam as regras do negócio
    private IGerenciadorVenda gVenda;
    private IGerenciadorOficina gOficina;
    private IGerenciadorRelatorio gRelatorio;
    private IGerenciadorTestDrive gTestDrive;

    // 2. CONSTRUTOR PADRÃO VAZIO
    public MenuPrincipal() {
    }

    /*
     * 3. MÉTODO DE INJEÇÃO (Substitui o construtor antigo com parâmetros)
     * Como o JavaFX cria a classe usando o construtor vazio acima, usamos este método
     * para que a classe 'Main' possa nos passar os gerenciadores logo após a tela abrir.
     */
    public void injetarGerenciadores(IGerenciadorVenda gV, IGerenciadorOficina gO, IGerenciadorRelatorio gR, IGerenciadorTestDrive gT) {
        this.gVenda = gV;
        this.gOficina = gO;
        this.gRelatorio = gR;
        this.gTestDrive = gT;
        System.out.println("-> [MenuPrincipal] Gerenciadores de negócio injetados com sucesso!");
    }

    /*
     * 4. MÉTODOS DOS BOTÕES (@FXML)
     * Substituem o antigo loop 'while' e o 'switch-case'. Agora, cada método acorda
     * apenas quando o botão correspondente for clicado fisicamente na tela.
     */

    @FXML
    public void tratarBotaoVenda() {
        System.out.println("-> [MenuPrincipal] Botão Tela de Venda clicado!");
        // Altera a janela atual de forma suave usando seu ScreenManager central
        ScreenManager.getInstance().showTelaVenda();
    }

    @FXML
    public void tratarBotaoOficina() {
        System.out.println("-> [MenuPrincipal] Botão Tela da Oficina clicado!");
        // (O processo aqui dentro será idêntico ao do botão de venda, mudando para o FXML da Oficina)
        ScreenManager.getInstance().showTelaOficina();
    }

    @FXML
    public void tratarBotaoTestDrive() {
        System.out.println("-> [MenuPrincipal] Botão Tela TestDrive clicado!");
        // (O processo aqui dentro será idêntico, mudando para o FXML do TestDrive)
        ScreenManager.getInstance().showTelaTestDrive();
    }

    @FXML
    public void tratarBotaoRelatorio() {
        System.out.println("-> [MenuPrincipal] Botão Tela de Relatórios clicado!");
        // (O processo aqui dentro será idêntico, mudando para o FXML de Relatórios)
        ScreenManager.getInstance().showTelaRelatorio();
    }

    @FXML
    public void tratarBotaoSair() {
        System.out.println("-> [MenuPrincipal] Fechando o sistema AutoDrive com segurança...");
        System.exit(0); // Fecha a aplicação instantaneamente
    }
}
