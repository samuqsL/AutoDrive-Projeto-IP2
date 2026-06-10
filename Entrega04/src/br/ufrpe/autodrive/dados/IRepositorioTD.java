package br.ufrpe.autodrive.dados;

import br.ufrpe.autodrive.negocio.beans.TestDrive;
import java.util.List;

public interface IRepositorioTD {
    void adicionarTestDrive(TestDrive td);
    List<TestDrive> listarTestDrives();
    TestDrive procurarTestDrive(String chassi);
    void removerTestDrive(String chassi);
    
    // remove test drive por ID
    TestDrive procurarTestDrivePorID(String id);
    void removerTestDrivePorID(String id);
}