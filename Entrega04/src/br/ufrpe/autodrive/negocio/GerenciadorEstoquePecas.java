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
            throw new Exception("A quantidade para reposição deve ser maior que zero.");
        }
        
        Pecas peca = repositorioPecas.buscarPorCodigo(codigo);
        
        if (peca == null) {
            throw new Exception("Peça não encontrada no estoque.");
        }
        
        int novaQuantidade = peca.getQuantidade() + quantidadeAdicional;
        boolean sucesso = repositorioPecas.alterarQuantidadeEstoque(codigo, novaQuantidade);
        
        if (!sucesso) {
            throw new Exception("Ocorreu um erro ao tentar salvar a nova quantidade no repositório.");
        }
    }
}
