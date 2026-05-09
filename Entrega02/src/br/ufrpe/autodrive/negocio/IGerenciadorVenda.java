package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.negocio.beans.Cliente;
import br.ufrpe.autodrive.negocio.beans.Vendedor;
import br.ufrpe.autodrive.negocio.beans.Veiculo;

public interface IGerenciadorVenda {

    /**
     * Tenta realizar uma venda.
     * @return true se passar em todas as regras de negócio e for salva, false caso contrário.
     */
    boolean efetuarVenda(Cliente c, Vendedor v, Veiculo veic, double entrada);

    // Você também pode adicionar outros métodos que a TelaVenda vai precisar
    // void cancelarVenda(String id);
    // Venda procurarVenda(String cpfCliente);
}
