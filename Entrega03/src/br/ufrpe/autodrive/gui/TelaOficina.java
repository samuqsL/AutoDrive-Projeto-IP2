package br.ufrpe.autodrive.gui;

import java.util.Scanner;
import br.ufrpe.autodrive.negocio.IGerenciadorOficina;

public class TelaOficina {
    private IGerenciadorOficina control;

    public TelaOficina(IGerenciadorOficina control) {
        this.control = control;
    }

    public void exibir() {
        Scanner leitor = new Scanner(System.in);
        int op = -1;

        while (op != 0) {
            System.out.println("\n--- OFICINA AUTO DRIVE ---");
            System.out.println("1. Abrir Ordem de Serviço");
            System.out.println("2. Finalizar Ordem de Serviço");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha: ");

            try {
                op = Integer.parseInt(leitor.nextLine());
                switch (op) {
                    case 1 -> botaoAbrirOS(leitor);
                    case 2 -> botaoFinalizarOS(leitor);
                    case 0 -> System.out.println("Saindo da Oficina...");
                    default -> System.out.println("Opção inválida!");
                }
            } catch (Exception e) {
                System.out.println("Erro: Digite apenas números.");
                op = -1;
            }
        }
    }

    private void botaoAbrirOS(Scanner leitor) {
        System.out.print("Digite o número da OS: ");
        int numero = Integer.parseInt(leitor.nextLine());
        System.out.print("Data (dd/mm/aaaa): ");
        String data = leitor.nextLine();
        System.out.print("CPF do Cliente: ");
        String cpf = leitor.nextLine();
        System.out.print("Chassi do Veículo: ");
        String chassi = leitor.nextLine();

        if (control.abrirOS(numero, data, cpf, chassi)) {
            System.out.println("✓ Sucesso: OS aberta e Veículo em manutenção.");
        } else {
            System.out.println("X Erro: Dados inválidos ou OS já existente.");
        }
    }

    private void botaoFinalizarOS(Scanner leitor) {
        System.out.print("Digite o número da OS para finalizar: ");
        int numero = Integer.parseInt(leitor.nextLine());

        if (control.finalizarServico(numero)) {
            System.out.println("✓ Sucesso: OS finalizada e Veículo disponível.");
        } else {
            System.out.println("X Erro: OS não paga ou falta óleo na revisão.");
        }
    }
}
