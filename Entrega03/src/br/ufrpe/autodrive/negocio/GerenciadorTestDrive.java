package br.ufrpe.autodrive.negocio;

import br.ufrpe.autodrive.dados.*;
import br.ufrpe.autodrive.negocio.beans.*;
import java.time.LocalDateTime;

public class GerenciadorTestDrive implements IGerenciadorTestDrive {
    private IRepositorioTD repoTD;
    private IRepositorioClientes repoC;
    private IRepositorioVeiculos repoV; 

    public GerenciadorTestDrive(IRepositorioTD repoTD, IRepositorioClientes repoC, IRepositorioVeiculos repoV) {
        this.repoTD = repoTD;
        this.repoC = repoC;
        this.repoV = repoV;
    }

    @Override
    public boolean agendarTestDrive(String cpf, String chassi) {
        Cliente c = repoC.procurarCliente(cpf);
        Veiculo v = repoV.procurarVeiculo(chassi); 

        if (c != null && v != null) {
            TestDrive novoTD = new TestDrive(c, v); // Usa o construtor padrão (pega LocalDateTime.now())
            if (novoTD.agendar()) {
                this.repoTD.adicionarTestDrive(novoTD);
                return true;
            }
        }
        return false;
    }

    // 🟢 Implementação da nova sobrecarga com data customizada
    @Override
    public boolean agendarTestDrive(String cpf, String chassi, LocalDateTime dataDigitada) {
        Cliente c = repoC.procurarCliente(cpf);
        Veiculo v = repoV.procurarVeiculo(chassi); 

        if (c != null && v != null) {
            // Se a data enviada for nula por algum motivo, garante o fallback para o momento atual
            if (dataDigitada == null) {
                dataDigitada = LocalDateTime.now();
            }
            
            // Usa o novo construtor passando a data capturada
            TestDrive novoTD = new TestDrive(c, v, dataDigitada); 
            if (novoTD.agendar()) {
                this.repoTD.adicionarTestDrive(novoTD);
                return true;
            }
        }
        return false;
    }
}
