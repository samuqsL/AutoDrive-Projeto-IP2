package br.ufrpe.autodrive.dados;

import br.ufrpe.autodrive.negocio.beans.TestDrive;
import java.util.ArrayList;
import java.util.List;

public class RepositorioTestDriveArray implements IRepositorioTD {

    private List<TestDrive> testDrives;

    public RepositorioTestDriveArray() {
        this.testDrives = new ArrayList<>();
    }

    @Override
    public void adicionarTestDrive(TestDrive td) {
        this.testDrives.add(td);
    }

    @Override
    public List<TestDrive> listarTestDrives() {
        return new ArrayList<TestDrive>(this.testDrives);
    }

    @Override
    public TestDrive procurarTestDrive(String chassi) {
        for (TestDrive td : testDrives) {
            if (td.getVeiculo().getChassi().equals(chassi)) {
                return td;
            }
        }
        return null;
    }

    @Override
    public void removerTestDrive(String chassi) {
        TestDrive tdEncontrado = procurarTestDrive(chassi);
        if (tdEncontrado != null) {
            this.testDrives.remove(tdEncontrado);
        }
    }
}
