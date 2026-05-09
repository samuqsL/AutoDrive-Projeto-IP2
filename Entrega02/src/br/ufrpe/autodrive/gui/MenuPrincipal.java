package br.ufrpe.autodrive.gui;

import br.ufrpe.autodrive.negocio.*; // Importa as Interfaces
import java.util.Scanner;

public class MenuPrincipal {
    // O Menu guarda os gerenciadores que recebeu do Main
    private IGerenciadorVenda gVenda;
    private IGerenciadorOficina gOficina;
    private IGerenciadorRelatorio gRelatorio;
    private IGerenciadorTestDrive gTestDrive;

    public MenuPrincipal(IGerenciadorVenda gV, IGerenciadorOficina gO, IGerenciadorRelatorio gR, IGerenciadorTestDrive gT) {
        this.gVenda = gV;
        this.gOficina = gO;
        this.gRelatorio = gR;
        this.gTestDrive = gT;
    }

    public void exibirMenu() {
    Scanner leitor = new Scanner(System.in); // Para ler a opção
    int op = -1; 

    // O loop deve continuar enquanto o usuário não escolher "Sair" (ex: 0)
    while (op != 0) {
        System.out.println("------------------------------------");
        System.out.println("-       OPÇÕES DO AUTODRIVE        -");
        System.out.println("------------------------------------");
        System.out.println("( 1 ) TELA DE VENDAS");
        System.out.println("( 2 ) TELA DA OFICINA");
        System.out.println("( 3 ) TELA DO TESTDRIVE");
        System.out.println("( 4 ) TELA DE RELATÓRIOS");
        System.out.println("( 0 ) SAIR");
        System.out.print("Escolha uma opção: ");
        
        op = leitor.nextInt(); // Lê a opção do usuário

        switch(op) {
            case 1:
                this.abrirTelaVenda(); // Chama o método que você já criou
                break;
            case 2:
                this.abrirTelaOficina();
                break;
            case 3:
                this.abrirTelaTestDrive();
                break;
            case 4:
                this.abrirTelaRelatorio();
                break;
            case 0:
                System.out.println("Saindo do sistema...");
                break;
            default:
                System.out.println("Opção inválida! Tente novamente.");
        }
    }
    // Métodos baseados no seu UML (image_81c45f.png)
    public void abrirTelaVenda() {
        // Cria a tela e passa o gerenciador específico para ela
        TelaVenda telaV = new TelaVenda(this.gVenda);
        telaV.exibir(); // Método na TelaVenda que inicia a interação
    }

    public void abrirTelaOficina() {
        TelaOficina telaO = new TelaOficina(this.gOficina);
        telaO.exibir();
    }

    public void abrirTelaRelatorio() {
        TelaRelatorio telaR = new TelaRelatorio(this.gRelatorio);
        telaR.exibir();
    }

    public void abrirTelaTestDrive() {
        TelaTestDrive TelaT = new TelaTestDrive(this.gTestDrive);
        TelaT.exibir();
    }
}
