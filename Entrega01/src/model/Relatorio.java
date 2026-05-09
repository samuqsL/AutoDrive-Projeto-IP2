package model;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Relatorio {
    private ArrayList<Venda> listaVendas;
    private ArrayList<OrdemServico> listaOs;

    public Relatorio() {
        this.listaVendas = new ArrayList<>();
        this.listaOs = new ArrayList<>();
    }
  
public Relatorio(ArrayList<Venda> listaVendas, OrdemServico os) {
    this.listaVendas = (listaVendas != null) ? listaVendas : new ArrayList<>();
    this.listaOs = new ArrayList<>();
    if (os != null) {
        this.listaOs.add(os);
    }
}


    public void adicionarVenda(Venda venda) {
        this.listaVendas.add(venda);
    }

    public void adicionarOS(OrdemServico os) {
        this.listaOs.add(os);
    }

    public void relatorioVendas() {
        System.out.println("--- Relatório Geral de Vendas ---");
        for (Venda v : listaVendas) {
            System.out.println("Venda: R$ " + v.getValorTotal() + " | Data: " + v.getDataVenda());
        }
    }

    public void vendasVendedor(Vendedor vendedor) {
        System.out.println("--- Vendas do Vendedor: " + vendedor.getNome() + " ---");
        for (Venda v : listaVendas) {
            if (v.getVendedor().getNome().equals(vendedor.getNome())) {
                System.out.println("Data: " + v.getDataVenda() + " | Valor: R$ " + v.getValorTotal());
            }
        }
    }

    public void vendasPeriodo(String inicio, String fim) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataIni = LocalDate.parse(inicio, fmt);
        LocalDate dataFim = LocalDate.parse(fim, fmt);

        System.out.println("--- Vendas de " + inicio + " a " + fim + " ---");
        for (Venda v : listaVendas) {
            if (v.getDataVenda() != null) {
                LocalDate dtVenda = v.getDataVenda().toLocalDate();
                if (!(dtVenda.isBefore(dataIni) || dtVenda.isAfter(dataFim))) {
                    System.out.println("Data: " + dtVenda + " | Valor: R$ " + v.getValorTotal());
                }
            }
        }
    }

    public void emitirRelatorioOS() {
        System.out.println("--- Relatório Geral da Oficina ---");
        for (OrdemServico os : listaOs) {
            System.out.println("OS Nº: " + os.getNumero() + " | Status: " + os.getStatus());
        }
    }
    
    public void relatorioLucratividadeOficina() {
       double lucroPecas = 0;
       double totalServicos = 0;

       for (OrdemServico os : listaOs) {
           for (Pecas p : os.getListaPecas()) {
               lucroPecas += p.custoPecas();
           }
           
           for (MaoDeObra m : os.getListaServicos()) {
               totalServicos += m.calcularCusto();
            }
       }
        
       System.out.println("\n--- RELATÓRIO DE LUCRATIVIDADE DA OFICINA ---");
       System.out.println("Receita com Peças: R$ " + String.format("%.2f", lucroPecas));
       System.out.println("Receita com Serviços: R$ " + String.format("%.2f", totalServicos));
       System.out.println("Total da Oficina: R$ " + String.format("%.2f", (lucroPecas + totalServicos)));
       System.out.println("----------------------------------------------");
    }
}
