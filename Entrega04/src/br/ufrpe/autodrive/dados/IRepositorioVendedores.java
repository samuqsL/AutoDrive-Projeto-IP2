package br.ufrpe.autodrive.dados;

import br.ufrpe.autodrive.negocio.beans.Vendedor;
import java.util.List;

public interface IRepositorioVendedores {
    void adicionarVendedor(Vendedor v);
    Vendedor procurarVendedor(String nome);
    void removerVendedor(String nome);
    List<Vendedor> listarTodos();
}
