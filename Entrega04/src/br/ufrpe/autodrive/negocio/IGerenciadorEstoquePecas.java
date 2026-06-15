package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.negocio.beans.Pecas;
import java.util.List;

public interface IGerenciadorEstoquePecas {
    List<Pecas> listarPecas();
    void reporEstoque(String codigo, int quantidadeAdicional) throws Exception;
}
