package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.dados.*;
import br.ufrpe.autodrive.negocio.beans.*;

public class GerenciadorTestDrive implements IGerenciadorTestDrive {
    private IRepositorioTD repoTD;
    private IRepositorioClientes repoC;
    private IRepositorioVeiculos repoV; // Agora usa o estoque central!

    public GerenciadorTestDrive(IRepositorioTD repoTD, IRepositorioClientes repoC, IRepositorioVeiculos repoV) {
        this.repoTD = repoTD;
        this.repoC = repoC;
        this.repoV = repoV;
    }

    @Override
    public boolean agendarTestDrive(String cpf, String chassi) {
        Cliente c = repoC.procurarCliente(cpf);
        Veiculo v = repoV.procurarVeiculo(chassi); // Busca no estoque real

        if (c != null && v != null) {
            TestDrive novoTD = new TestDrive(c, v);
            // A Bean TestDrive valida CNH e Status do Veículo
            if (novoTD.agendar()) {
                this.repoTD.adicionarTestDrive(novoTD);
                return true;
            }
        }
        return false;
    }
}
