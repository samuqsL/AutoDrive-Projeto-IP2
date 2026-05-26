package br.ufrpe.autodrive.dados;

import br.ufrpe.autodrive.negocio.beans.Vendedor;
import java.util.ArrayList;
import java.util.List;

public class RepositorioVendedoresArray implements IRepositorioVendedores {
    private List<Vendedor> estoqueVendedores;

    public RepositorioVendedoresArray() {
        this.estoqueVendedores = new ArrayList<>();
    }

    @Override
    public void adicionarVendedor(Vendedor v) {
        if (v != null) {
            this.estoqueVendedores.add(v);
        }
    }

    @Override
    public Vendedor procurarVendedor(String nome) {
        for (Vendedor v : estoqueVendedores) {
            if (v.getNome().equalsIgnoreCase(nome)) {
                return v;
            }
        }
        return null;
    }

    @Override
    public void removerVendedor(String nome) {
        Vendedor v = procurarVendedor(nome);
        if (v != null) {
            this.estoqueVendedores.remove(v);
        }
    }

    @Override
    public List<Vendedor> listarTodos() {
        return new ArrayList<>(estoqueVendedores);
    }
}
