package br.ufrpe.autodrive.dados;

import br.ufrpe.autodrive.negocio.beans.Veiculo;
import java.util.List;

public interface IRepositorioVeiculos {
    void adicionarVeiculo(Veiculo v);
    Veiculo procurarVeiculo(String chassi); // Busca por Chassi (ID único)
    List<Veiculo> listarTodos();
    void removerVeiculo(String chassi);
}
