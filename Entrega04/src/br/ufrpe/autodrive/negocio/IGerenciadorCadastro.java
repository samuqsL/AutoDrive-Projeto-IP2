package br.ufrpe.autodrive.negocio;

import java.util.List;
import br.ufrpe.autodrive.negocio.beans.Cliente;
import br.ufrpe.autodrive.negocio.beans.Veiculo;

public interface IGerenciadorCadastro {
    
    // Métodos Principais de Regra de Negócio
    void cadastrarCliente(String nome, String cpf, String cnh, String email, String telefone) throws Exception;
    
    void cadastrarVeiculo(String chassi, String renavam, String modelo, int ano, double preco, boolean ehSeminovo, double kmInicial) throws Exception;
    
    // Métodos Auxiliares para alimentar as Tabelas da Janela
    List<Cliente> listarClientes();
    List<Veiculo> listarVeiculos();
}
