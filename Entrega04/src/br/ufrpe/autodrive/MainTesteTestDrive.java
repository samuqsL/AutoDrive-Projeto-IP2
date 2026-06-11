package br.ufrpe.autodrive;

import br.ufrpe.autodrive.dados.*;
import br.ufrpe.autodrive.negocio.*;
import br.ufrpe.autodrive.negocio.beans.*;
import java.time.LocalDateTime;

public class MainTesteTestDrive {
    public static void main(String[] args) {
        System.out.println("=== HOMOLOGAÇÃO ISOLADA: MÓDULO TEST-DRIVE ===");

        // 1. Inicializando Repositórios (Usando os Singletons reais do seu projeto)
        IRepositorioTD repoTD = RepositorioTestDriveArray.getInstance();
        IRepositorioClientes repoClientes = RepositorioClientesArray.getInstance();
        IRepositorioVeiculos repoVeiculos = RepositorioVeiculosArray.getInstance();

        GerenciadorTestDrive gerenciador = new GerenciadorTestDrive(repoTD, repoClientes, repoVeiculos);

        // 2. Criando massa de dados limpa com prefixo "TD_"
        String cpfValido = "11122233344";
        Cliente c1 = new Cliente("Motorista Teste", cpfValido, "999999");
        c1.setCnh("123456789"); // Garante CNH preenchida para passar no validador
        repoClientes.adicionarCliente(c1);

        Veiculo v1 = new VeiculoNovo("CHASSI_TD1", "AAA-1111", "Civic TD", 2023, 150000.0);
        v1.setStatus(StatusVeiculo.DISPONIVEL);
        repoVeiculos.adicionarVeiculo(v1);

        Veiculo vManutencao = new VeiculoNovo("CHASSI_TD_MANUT", "BBB-2222", "Carro Quebrado", 2022, 80000.0);
        vManutencao.setStatus(StatusVeiculo.EM_MANUTENCAO);
        repoVeiculos.adicionarVeiculo(vManutencao);

        // ----------------------------------------------------------------------
        System.out.println("\n--- PASSO 1: Agendando Test-Drive Válido ---");
        LocalDateTime horaAgendamento = LocalDateTime.of(2026, 6, 15, 14, 0); // 15/06/2026 às 14:00
        
        boolean agendou1 = gerenciador.agendarTestDrive(cpfValido, "CHASSI_TD1", horaAgendamento);
        System.out.println("Agendamento 1 realizado? " + agendou1 + " (Esperado: true)");
        System.out.println("Status atual do veículo: " + v1.getStatus() + " (Esperado: TEST_DRIVE)");

        // ----------------------------------------------------------------------
        System.out.println("\n--- PASSO 2: Testando Bloqueio de Carro em Manutenção ---");
        boolean agendouInvalido = gerenciador.agendarTestDrive(cpfValido, "CHASSI_TD_MANUT", horaAgendamento);
        System.out.println("Agendou carro em manutenção? " + agendouInvalido + " (Esperado: false)");

        // ----------------------------------------------------------------------
        System.out.println("\n--- PASSO 3: Testando Bloqueio de Conflito de Horário (Janela de 1h) ---");
        // Tentando agendar o MESMO carro às 14:30 (dentro da janela de 1 hora do primeiro agendamento)
        LocalDateTime horaConflitante = LocalDateTime.of(2026, 6, 15, 14, 30);
        
        boolean agendouConflito = gerenciador.agendarTestDrive(cpfValido, "CHASSI_TD1", horaConflitante);
        System.out.println("Agendou com horário conflitante? " + agendouConflito + " (Esperado: false)");

        // ----------------------------------------------------------------------
        System.out.println("\n--- PASSO 4: Listando os Agendamentos Confirmados em Disco ---");
        for (TestDrive td : gerenciador.listarTestDrives()) {
            if (td.getVeiculo().getChassi().startsWith("CHASSI_TD")) {
                System.out.println("ID Agendamento: " + td.getId() + 
                                   " | Cliente: " + td.getCliente().getNome() + 
                                   " | Veículo: " + td.getVeiculo().getModelo() +
                                   " | Data/Hora: " + td.getDataTestDrive());
            }
        }
    }
}