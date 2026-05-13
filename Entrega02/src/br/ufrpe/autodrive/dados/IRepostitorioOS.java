package br.ufrpe.autodrive.dados;

import java.util.List;
import br.ufrpe.autodrive.negocio.beans.OrdemServico;

public interface IRepositorioOS {
    
    void salvar(OrdemServico os);
    
    List<OrdemServico> listarTodas();
    
    OrdemServico buscarPorNumero(int numero);
    
    void remover(int numero);
}
