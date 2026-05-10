package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.dados.IRepositorioTD;
import br.ufrpe.autodrive.dados.IRepositorioClientes;
import br.ufrpe.autodrive.dados.IRepositorioVeiculos; // Importe o Repo de Veículos do Artur
import br.ufrpe.autodrive.negocio.beans.*;

public class GerenciadorTestDrive implements IGerenciadorTestDrive {
    
    private IRepositorioTD repoTD;
    private IRepositorioClientes repoC; // Injeção de dependência necessária
    private IRepositorioVeiculos repoV; // Injeção de dependência necessária

    public GerenciadorTestDrive(IRepositorioTD repoTD, IRepositorioClientes repoC, IRepositorioVeiculos repoV) {
        this.repoTD = repoTD;
        this.repoC = repoC;
        this.repoV = repoV;
    }

    @Override
    public boolean agendarTestDrive(String cpf, String chassi) {
        // Busca os objetos reais nos repositórios
        Cliente c = repoC.procurarCliente(cpf);
        Veiculo v = repoV.procurarVeiculo(chassi); // Use o método de busca que o Artur criou

        if (c != null && v != null) {
            TestDrive novoTD = new TestDrive(c, v);
            
            // A Bean valida CNH e Status do Veículo
            if (novoTD.agendar()) {
                this.repoTD.adicionarTestDrive(novoTD);
                return true;
            }
        }
        return false; // Retorna false se cliente/veículo não existirem ou se a validação falhar
    }
}
