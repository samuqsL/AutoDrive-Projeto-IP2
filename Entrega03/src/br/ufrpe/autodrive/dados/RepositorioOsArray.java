package br.ufrpe.autodrive.dados;

import java.util.ArrayList;
import java.util.List;
import br.ufrpe.autodrive.negocio.beans.OrdemServico;

public class RepositorioOsArray implements IRepositorioOS {
    
    // armazenar os objetos Os
    private List<OrdemServico> listaOS;

    public RepositorioOsArray() {
        this.listaOS = new ArrayList<>();
    }

    @Override
    public void salvar(OrdemServico os) {
        if (os != null) {
            OrdemServico osExistente = buscarPorNumero(os.getNumero());
            if (osExistente != null) {
                int indice = this.listaOS.indexOf(osExistente);
                this.listaOS.set(indice, os);
            } else {
                this.listaOS.add(os);
            }
        }
    }

    @Override
    public List<OrdemServico> listarTodas() {
        return new ArrayList<>(this.listaOS);
    }

    @Override
    public OrdemServico buscarPorNumero(int numero) {
        for (OrdemServico os : this.listaOS) {
            if (os.getNumero() == numero) {
                return os; 
            }
        }
        return null; // se não encontrar o Os
    }

    @Override
    public void remover(int numero) {
        OrdemServico osParaRemover = buscarPorNumero(numero);
        if (osParaRemover != null) {
            this.listaOS.remove(osParaRemover);
        }
    }
}
