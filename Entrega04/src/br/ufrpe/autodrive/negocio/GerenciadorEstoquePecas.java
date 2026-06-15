package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.dados.IRepositorioPecas;
import br.ufrpe.autodrive.negocio.beans.Pecas;
import java.util.List;

public class GerenciadorEstoquePecas implements IGerenciadorEstoquePecas {

    private IRepositorioPecas repositorioPecas;

    public GerenciadorEstoquePecas(IRepositorioPecas repositorioPecas) {
        this.repositorioPecas = repositorioPecas;
    }

    @Override
    public List<Pecas> listarPecas() {
        return repositorioPecas.listarTodas();
    }

    @Override
    public void reporEstoque(String codigo, int quantidadeAdicional) throws Exception {
        if (quantidadeAdicional <= 0) {
            throw new Exception("A quantidade para repor deve ser maior que zero.");
        }

        Pecas peca = repositorioPecas.buscarPorCodigo(codigo);
        
        // A peça sempre existirá pois foi selecionada via ComboBox na UI
        if (peca != null) {
            int novaQuantidade = peca.getQuantidade() + quantidadeAdicional;
            repositorioPecas.alterarQuantidadeEstoque(codigo, novaQuantidade);
        }
    }
}
