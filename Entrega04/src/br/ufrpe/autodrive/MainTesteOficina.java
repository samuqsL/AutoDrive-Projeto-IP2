package br.ufrpe.autodrive;

import br.ufrpe.autodrive.dados.*;
import br.ufrpe.autodrive.negocio.*;
import br.ufrpe.autodrive.negocio.beans.*;

public class MainTesteOficina {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO HOMOLOGAÇÃO ISOLADA DO SISTEMA ===");

        // 1. Inicializando Repositórios
        IRepositorioOS repoOS = RepositorioOsArray.getInstance();
        IRepositorioClientes repoClientes = RepositorioClientesArray.getInstance();
        IRepositorioVeiculos repoVeiculos = RepositorioVeiculosArray.getInstance();
        IRepositorioMecanicos repoMecanicos = RepositorioMecanicosArray.getInstance();

        GerenciadorOficina gerenciador = new GerenciadorOficina(repoOS, repoClientes, repoVeiculos, repoMecanicos);
        
        // 2. Massa de dados ÚNICA (com o prefixo "T_") para não misturar com o passado
        String cpfTeste = "99988877711";
        Cliente c1 = new Cliente("Cliente Teste FIFO", cpfTeste, "1234567");
        repoClientes.adicionarCliente(c1);

        Mecanico m1 = new Mecanico("Mecanico_T1", true); 
        Mecanico m2 = new Mecanico("Mecanico_T2", true); 
        repoMecanicos.adicionarMecanico(m1);
        repoMecanicos.adicionarMecanico(m2);

        Veiculo v1 = new VeiculoNovo("CHASSI_T1", "123", "Uno Teste", 2020, 30000.0);
        Veiculo v2 = new VeiculoNovo("CHASSI_T2", "456", "Palio Teste", 2021, 35000.0);
        Veiculo v3 = new VeiculoNovo("CHASSI_T3", "789", "Gol Teste", 2022, 40000.0);
        
        v1.setStatus(StatusVeiculo.DISPONIVEL);
        v2.setStatus(StatusVeiculo.DISPONIVEL);
        v3.setStatus(StatusVeiculo.DISPONIVEL);
        
        repoVeiculos.adicionarVeiculo(v1);
        repoVeiculos.adicionarVeiculo(v2);
        repoVeiculos.adicionarVeiculo(v3);

        // ----------------------------------------------------------------------
        System.out.println("\n--- PASSO 1: Abrindo OS 1 e OS 2 (Devem pegar os 2 mecânicos de teste) ---");
        gerenciador.abrirOS(cpfTeste, "CHASSI_T1");
        gerenciador.abrirOS(cpfTeste, "CHASSI_T2");

        for (OrdemServico os : repoOS.listarTodas()) {
            if (os.getVeiculo().getChassi().startsWith("CHASSI_T")) {
                System.out.println("OS Nº: " + os.getNumero() + " | Veículo: " + os.getVeiculo().getModelo() + 
                                   " | Status: " + os.getStatus() + " | Mecânico: " + (os.getMecanico() != null ? os.getMecanico().getNome() : "Nenhum"));
            }
        }

        // ----------------------------------------------------------------------
        System.out.println("\n--- PASSO 2: Abrindo OS 3 (Mecânicos ocupados, deve ir para a FILA em ABERTA) ---");
        gerenciador.abrirOS(cpfTeste, "CHASSI_T3");
        
        for (OrdemServico os : repoOS.listarTodas()) {
            if (os.getVeiculo().getChassi().equals("CHASSI_T3")) {
                System.out.println("OS 3 Cadastrada -> Status Real: " + os.getStatus() + " (Esperado: ABERTA)");
            }
        }

        // ----------------------------------------------------------------------
        System.out.println("\n--- PASSO 3: Adicionando Serviços e Peças na OS correta ---");
        OrdemServico os1 = null;
        for (OrdemServico os : repoOS.listarTodas()) {
            if (os.getVeiculo().getChassi().equals("CHASSI_T1")) {
                os1 = os;
                break;
            }
        }

        if (os1 != null) {
            int numOS1 = os1.getNumero();
            System.out.println("Modificando OS Nº: " + numOS1 + " (Status Atual: " + os1.getStatus() + ")");

            Pecas pastilha = new Pecas("Pastilha de Freio", "P01", 50.0, 10);
            
            Mecanico mecOS1 = os1.getMecanico();
            if (mecOS1 != null) {
                mecOS1.setDisponivel(true); 
                MaoDeObra trocaFreio = new MaoDeObra("Troca de Freios", 150.0, 1, mecOS1);
                mecOS1.setDisponivel(false); 

                gerenciador.registrarPecaNaOS(numOS1, pastilha, 1); 
                gerenciador.registrarServicoNaOS(numOS1, trocaFreio); 
                
                // ----------------------------------------------------------------------
                System.out.println("\n--- PASSO 4: Finalizando a OS 1 e testando Desbloqueio da Fila ---");
                boolean finalizou = gerenciador.finalizarServico(numOS1);
                
                System.out.println("OS 1 finalizada com sucesso? " + finalizou);
                System.out.println("Valor Total Calculado da OS 1: R$ " + os1.getValorTotal());
                System.out.println("*(Esperado: R$ 320.00)*");
                System.out.println("Produtividade do Mecânico " + mecOS1.getNome() + ": " + mecOS1.getProdutividade());
            } else {
                System.out.println("ERRO: Nenhum mecânico foi alocado para a OS 1!");
            }
        } else {
            System.out.println("ERRO: OS 1 de teste não encontrada.");
        }

        // ----------------------------------------------------------------------
        System.out.println("\n--- PASSO 5: Verificando se a OS 3 desencalhou da Fila Sozinha ---");
        for (OrdemServico os : repoOS.listarTodas()) {
            if (os.getVeiculo().getChassi().equals("CHASSI_T3")) {
                System.out.println("Status Atual da OS 3: " + os.getStatus() + 
                                   " | Mecânico Alocado: " + (os.getMecanico() != null ? os.getMecanico().getNome() : "Nenhum"));
            }
        }
    }
}