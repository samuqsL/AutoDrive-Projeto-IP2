package br.ufrpe.autodrive.negocio;

import java.time.LocalDateTime;
import java.util.List;

import br.ufrpe.autodrive.negocio.beans.Cliente;
import br.ufrpe.autodrive.negocio.beans.TestDrive;
import br.ufrpe.autodrive.negocio.beans.Veiculo;

public interface IGerenciadorTestDrive {
    boolean agendarTestDrive(String cpf, String chassi);
    boolean agendarTestDrive(String cpf, String chassi, LocalDateTime dataDigitada);
    List<TestDrive> listarTestDrives();
    
    List<Cliente> listarTodosClientes();
    List<Veiculo> listarTodosVeiculos();
    boolean cancelarTestDrive(String id);
}
