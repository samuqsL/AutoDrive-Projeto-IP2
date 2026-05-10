package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.negocio.beans.Cliente;
import br.ufrpe.autodrive.negocio.beans.Veiculo;

public interface IGerenciadorTestDrive {
    boolean agendarTestDrive(Cliente cliente, Veiculo veiculo);
}
