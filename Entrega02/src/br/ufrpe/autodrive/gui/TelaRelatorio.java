package br.ufrpe.autodrive.gui;

import java.util.Scanner;
import java.util.List;
import br.ufrpe.autodrive.negocio.IGerenciadorRelatorio;
import br.ufrpe.autodrive.negocio.beans.Venda;
import br.ufrpe.autodrive.negocio.beans.OrdemServico;

public class TelaRelatorio {
    private IGerenciadorRelatorio control;

    public TelaRelatorio(IGerenciadorRelatorio control) {
        this.control = control;
    }

    public void exibir() {
        Scanner leitor = new Scanner(System.in);
        int op = -1;

        while (op != 0) {
            System.out.println("\n--- SISTEMA DE RELATÓRIOS ---");
            System.out.println("1. Relatório Geral de Vendas");
            System.out.println("2. Relatório Geral de Oficina (OS)");
            System.out.println("0. Voltar");
            System.out.print("Escolha: ");

            if (leitor.hasNextInt()) {
                op = leitor.nextInt();
            } else {
                leitor.next(); 
                continue;
            }

            if (op == 1) this.botaoRelatorioVendas();
            if (op == 2) this.botaoRelatorioOS();
        }
    }

    public void botaoRelatorioVendas() {
        List<Venda> vendas = control.gerarRelatorioVendas();
        
        System.out.println("\n--- Relatório Geral de Vendas ---");
        
        
        if (vendas == null || vendas.size() == 0) {
            System.out.println("Nenhuma venda cadastrada no sistema.");
        } else {
            for (Venda v : vendas) {
                System.out.println("Venda: R$ " + v.getValorTotal() + " | Data: " + v.getDataVenda());
            }
        }
    }

    public void botaoRelatorioOS() {
        List<OrdemServico> ordens = control.gerarRelatorioOS();
        
        System.out.println("\n--- Relatório Geral da Oficina ---");
        
        
        if (ordens == null || ordens.size() == 0) {
            System.out.println("Nenhuma Ordem de Serviço encontrada.");
        } else {
            for (OrdemServico os : ordens) {
                System.out.println("OS Nº: " + os.getNumero() + " | Status: " + os.getStatus());
            }
        }
    }
}
