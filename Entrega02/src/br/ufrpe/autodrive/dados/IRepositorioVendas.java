package br.ufrpe.autodrive.dados;

import br.ufrpe.autodrive.negocio.beans.Venda;
import java.util.List;

public interface IRepositorioVendas {
    void adicionarVenda(Venda venda);
    Venda procurarVenda(String cpf);
    void removerVenda(int numero);
    List<Venda> listarTodasVendas();
}
