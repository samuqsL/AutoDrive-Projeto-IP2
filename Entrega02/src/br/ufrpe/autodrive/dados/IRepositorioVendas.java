package br.ufrpe.autodrive.dados;

import br.ufrpe.autodrive.negocio.beans.Venda;
import java.util.List;

public interface IRepositorioVendas {
    void adicionarVenda(Venda venda); 
    Venda procurarVenda(int numero);  // pelo numero 'int'
    void removerVenda(int numero);    // pelo numero 'int'
    List<Venda> listarTodasVendas();
}
