package br.ufrpe.autodrive.gui;

import br.ufrpe.autodrive.negocio.IGerenciadorTestDrive;
import java.util.Scanner;

public class TelaTestDrive {

    private IGerenciadorTestDrive control;

    public TelaTestDrive(IGerenciadorTestDrive control) {
        this.control = control;
    }

    public void exibir() {
        Scanner scanner = new Scanner(System.in);
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n--- TELA DE TEST-DRIVE ---");
            System.out.println("1 - Agendar Test-Drive");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine());
                switch (opcao) {
                    case 1: BotaoAgendarTestDrive(scanner); break;
                    case 0: System.out.println("Saindo..."); break;
                    default: System.out.println("Opção inválida!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido!");
            }
        }
    }

    private void BotaoAgendarTestDrive(Scanner scanner) {
        System.out.println("\n--- NOVO AGENDAMENTO ---");
        System.out.print("CPF do Cliente: ");
        String cpf = scanner.nextLine();
        System.out.print("Chassi do Veículo: ");
        String chassi = scanner.nextLine();
    
        // Chama o gerenciador passando apenas o que foi digitado
        if (this.control.agendarTestDrive(cpf, chassi)) {
            System.out.println(">>> SUCESSO: Agendamento realizado!");
        } else {
            System.out.println(">>> ERRO: Cliente não encontrado, Chassi inválido ou CNH insuficiente.");
        }
    }
}
