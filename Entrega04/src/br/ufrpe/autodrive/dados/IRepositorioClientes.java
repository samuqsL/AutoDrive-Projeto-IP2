package br.ufrpe.autodrive.dados;

import java.util.List;
import br.ufrpe.autodrive.negocio.beans.Cliente;

public interface IRepositorioClientes {
    void adicionarCliente(Cliente novoCliente);
    Cliente procurarCliente(String cpf);
    void removerCliente(Cliente cliente);
    List<Cliente> listarClientes();
}
