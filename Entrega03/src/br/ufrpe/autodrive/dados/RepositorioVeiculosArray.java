package br.ufrpe.autodrive.dados;

import br.ufrpe.autodrive.negocio.beans.Veiculo;
import java.util.ArrayList;
import java.util.List;

public class RepositorioVeiculosArray implements IRepositorioVeiculos {
    private List<Veiculo> estoque;

    public RepositorioVeiculosArray() {
        this.estoque = new ArrayList<>();
    }

    @Override
    public void adicionarVeiculo(Veiculo v) {
        this.estoque.add(v);
    }

    @Override
    public Veiculo procurarVeiculo(String chassi) {
        for (Veiculo v : estoque) {
            if (v.getChassi().equals(chassi)) return v;
        }
        return null;
    }

    @Override
    public List<Veiculo> listarTodos() {
        return new ArrayList<Veiculo>(this.estoque);
    }

    @Override
    public void removerVeiculo(String chassi) {
        Veiculo v = procurarVeiculo(chassi);
        if (v != null) estoque.remove(v);
    }
}
