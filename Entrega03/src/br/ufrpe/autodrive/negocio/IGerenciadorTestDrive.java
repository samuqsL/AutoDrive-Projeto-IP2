package br.ufrpe.autodrive.negocio;

import java.time.LocalDateTime;
import java.util.List;

import br.ufrpe.autodrive.negocio.beans.Cliente;
import br.ufrpe.autodrive.negocio.beans.TestDrive;
import br.ufrpe.autodrive.negocio.beans.Veiculo;

public interface IGerenciadorTestDrive {
    boolean agendarTestDrive(String cpf, String chassi);
    
    // Nova sobrecarga que aceita a data vinda da interface gráfica
    boolean agendarTestDrive(String cpf, String chassi, LocalDateTime dataDigitada);
    
    public List<TestDrive> listarTestDrives();
    
    // metodos novos
    List<Cliente> listarTodosClientes();
    List<Veiculo> listarTodosVeiculos();
    boolean cancelarTestDrive(String hash);
}
