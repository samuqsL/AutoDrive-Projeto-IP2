package br.ufrpe.autodrive.dados;

import br.ufrpe.autodrive.negocio.beans.Pecas;
import java.util.List;

public interface IRepositorioPecas {
    void salvar(Pecas peca);
    List<Pecas> listarTodas();
    Pecas buscarPorCodigo(String codigo);
    void removerPorCodigo(String codigo);
    boolean alterarQuantidadeEstoque(String codigo, int novaQtd); // Casado com o repositório
}
