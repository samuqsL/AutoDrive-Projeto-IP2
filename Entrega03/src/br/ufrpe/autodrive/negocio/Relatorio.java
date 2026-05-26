package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.negocio.beans.Venda;
import br.ufrpe.autodrive.negocio.beans.OrdemServico;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class Relatorio {
    private List<Venda> listaVendas;
    private List<OrdemServico> listaOs;

    public Relatorio(List<Venda> vendas, List<OrdemServico> ordens) {
        this.listaVendas = (vendas != null) ? new ArrayList<>(vendas) : new ArrayList<>();
        this.listaOs = (ordens != null) ? new ArrayList<>(ordens) : new ArrayList<>();
    }

    public List<Venda> getListaVendas() { return listaVendas; }
    public List<OrdemServico> getListaOs() { return listaOs; }

    // Filtra vendas por nome do vendedor (ignora maiúsculas/minúsculas)
    public List<Venda> filtrarPorVendedor(String nomeVendedor) {
        List<Venda> filtradas = new ArrayList<>();
        for (Venda v : listaVendas) {
            if (v.getVendedor().getNome().equalsIgnoreCase(nomeVendedor)) {
                filtradas.add(v);
            }
        }
        return filtradas;
    }

    // Filtra vendas entre duas datas
    public List<Venda> filtrarPorPeriodo(LocalDate inicio, LocalDate fim) {
        List<Venda> filtradas = new ArrayList<>();
        for (Venda v : listaVendas) {
            LocalDate dataVenda = v.getDataVenda().toLocalDate();
            if (!(dataVenda.isBefore(inicio) || dataVenda.isAfter(fim))) {
                filtradas.add(v);
            }
        }
        return filtradas;
    }

    public double[] calcularLucratividade() {
        double receitaPecas = 0;
        double receitaServicos = 0;

        for (OrdemServico os : listaOs) {
            receitaPecas += os.getValorPecas(); 
            receitaServicos += os.getValorMaoDeObra();
        }
        return new double[]{receitaPecas, receitaServicos};
    }
}
