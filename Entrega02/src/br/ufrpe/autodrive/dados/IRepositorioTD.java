package br.ufrpe.autodrive.dados;

import br.ufrpe.autodrive.negocio.beans.TestDrive;
import java.util.List;

public interface IRepositorioTD {
    void adicionarTestDrive(TestDrive td);
    List<TestDrive> listarTestDrives();
}
