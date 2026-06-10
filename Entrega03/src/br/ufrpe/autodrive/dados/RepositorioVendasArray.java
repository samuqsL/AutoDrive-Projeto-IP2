package br.ufrpe.autodrive.dados;

import br.ufrpe.autodrive.negocio.beans.Venda;
import java.util.ArrayList;
import java.util.List;

public class RepositorioVendasArray implements IRepositorioVendas {
    private List<Venda> ListaDeVendas;

    public RepositorioVendasArray() {
        this.ListaDeVendas = new ArrayList<>();
    }

    @Override
    public void adicionarVenda(Venda venda) {
        if (venda != null) {
            this.ListaDeVendas.add(venda);
        }
    }

    @Override
    public Venda procurarVenda(int numero) {
        for (Venda v : ListaDeVendas) {
            // Correção: Tipos primitivos (int) comparam com ==
            if (v.getNumero() == numero) {
                return v;
            }
        }
        return null;
    }

    @Override
    public void removerVenda(int numero) {
        Venda alvo = procurarVenda(numero); // Usa o método de busca acima
        if (alvo != null) {
            this.ListaDeVendas.remove(alvo);
        }
    }

    @Override
    public List<Venda> listarTodasVendas() {
        return new ArrayList<>(ListaDeVendas);
    }
}
