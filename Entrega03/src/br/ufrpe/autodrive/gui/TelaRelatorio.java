package br.ufrpe.autodrive.gui;

import br.ufrpe.autodrive.negocio.IGerenciadorRelatorio;
import br.ufrpe.autodrive.negocio.Relatorio; // <-- O import correto agora
import br.ufrpe.autodrive.negocio.beans.Venda;
import br.ufrpe.autodrive.negocio.beans.OrdemServico;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TelaRelatorio {
    private IGerenciadorRelatorio control;
    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TelaRelatorio(IGerenciadorRelatorio control) {
        this.control = control;
    }

    public void exibir() {
        Scanner leitor = new Scanner(System.in);
        String op = "-1";

        while (!op.equals("0")) {
            System.out.println("\n--- AUTO DRIVE - RELATÓRIOS ---");
            System.out.println("1. Vendas Gerais");
            System.out.println("2. Vendas por Vendedor");
            System.out.println("3. Vendas por Período");
            System.out.println("4. Geral de Oficina (OS)");
            System.out.println("5. Lucratividade (Peças/Serviços)");
            System.out.println("0. Voltar");
            System.out.print("Opção: ");
            
            op = leitor.nextLine();
            
            switch (op) {
                case "1" -> {
                    Relatorio dados = control.gerarDadosRelatorio();
                    imprimirVendas(dados.getListaVendas(), "GERAL");
                }
                case "2" -> {
                    System.out.print("Nome do Vendedor: ");
                    String nome = leitor.nextLine();
                    Relatorio dados = control.gerarDadosRelatorio();
                    imprimirVendas(dados.filtrarPorVendedor(nome), "VENDEDOR: " + nome);
                }
                case "3" -> {
                    try {
                        System.out.print("Data Início (dd/mm/aaaa): ");
                        LocalDate ini = LocalDate.parse(leitor.nextLine(), fmt);
                        System.out.print("Data Fim (dd/mm/aaaa): ");
                        LocalDate fim = LocalDate.parse(leitor.nextLine(), fmt);
                        Relatorio dados = control.gerarDadosRelatorio();
                        imprimirVendas(dados.filtrarPorPeriodo(ini, fim), "PERÍODO");
                    } catch (Exception e) { System.out.println("Erro: Data inválida!"); }
                }
                case "4" -> {
                    Relatorio dados = control.gerarDadosRelatorio();
                    imprimirOS(dados.getListaOs());
                }
                case "5" -> {
                    double[] lucros = control.gerarDadosRelatorio().calcularLucratividade();
                    System.out.println("\n--- RESUMO FINANCEIRO ---");
                    System.out.printf("Receita de Peças: R$ %.2f\n", lucros[0]);
                    System.out.printf("Receita de Serviços: R$ %.2f\n", lucros[1]);
                    System.out.printf("TOTAL ACUMULADO: R$ %.2f\n", (lucros[0] + lucros[1]));
                }
            }
        }
    }

    private void imprimirVendas(List<Venda> lista, String titulo) {
        System.out.println("\n--- RELATÓRIO DE VENDAS (" + titulo + ") ---");
        if (lista.isEmpty()) System.out.println("Nenhum registro encontrado.");
        for (Venda v : lista) {
            System.out.println("Nº: " + v.getNumero() + " | Vendedor: " + v.getVendedor().getNome() + 
                               " | Total: R$ " + v.getValorTotal());
        }
    }

    private void imprimirOS(List<OrdemServico> lista) {
        System.out.println("\n--- RELATÓRIO DE OFICINA ---");
        if (lista.isEmpty()) System.out.println("Nenhuma OS encontrada.");
        for (OrdemServico os : lista) {
            System.out.println("OS Nº: " + os.getNumero() + " | Cliente: " + os.getCliente().getNome() + 
                               " | Status: " + os.getStatus());
        }
    }
}
