package br.ufrpe.autodrive;

import java.time.LocalDateTime;

import br.ufrpe.autodrive.dados.IRepositorioClientes;
import br.ufrpe.autodrive.dados.IRepositorioMecanicos;
import br.ufrpe.autodrive.dados.IRepositorioOS;
import br.ufrpe.autodrive.dados.IRepositorioTD;
import br.ufrpe.autodrive.dados.IRepositorioVeiculos;
import br.ufrpe.autodrive.dados.IRepositorioVendas;
import br.ufrpe.autodrive.dados.IRepositorioVendedores;
import br.ufrpe.autodrive.dados.RepositorioClientesArray;
import br.ufrpe.autodrive.dados.RepositorioMecanicosArray;
import br.ufrpe.autodrive.dados.RepositorioOsArray;
import br.ufrpe.autodrive.dados.RepositorioTestDriveArray;
import br.ufrpe.autodrive.dados.RepositorioVeiculosArray;
import br.ufrpe.autodrive.dados.RepositorioVendasArray;
import br.ufrpe.autodrive.dados.RepositorioVendedoresArray;
import br.ufrpe.autodrive.gui.ScreenManager;
import br.ufrpe.autodrive.negocio.GerenciadorOficina;
import br.ufrpe.autodrive.negocio.GerenciadorRelatorio;
import br.ufrpe.autodrive.negocio.GerenciadorTestDrive;
import br.ufrpe.autodrive.negocio.GerenciadorVenda;
import br.ufrpe.autodrive.negocio.IGerenciadorOficina;
import br.ufrpe.autodrive.negocio.IGerenciadorRelatorio;
import br.ufrpe.autodrive.negocio.IGerenciadorTestDrive;
import br.ufrpe.autodrive.negocio.IGerenciadorVenda;
import br.ufrpe.autodrive.negocio.beans.Cliente;
import br.ufrpe.autodrive.negocio.beans.Mecanico;
import br.ufrpe.autodrive.negocio.beans.OrdemServico;
import br.ufrpe.autodrive.negocio.beans.VeiculoNovo;
import br.ufrpe.autodrive.negocio.beans.VeiculoSeminovo;
import br.ufrpe.autodrive.negocio.beans.Vendedor;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // =========================================================================
        // 🟢 Passo 1: Inicialização dos Repositórios (Singleton + Persistência)
        // =========================================================================
        
        // REPO DA PARTE DE VENDAS (Já atualizados com Singleton e salvamento automático)
        IRepositorioVendas repoVendas = RepositorioVendasArray.getInstance();
        IRepositorioClientes repoClientes = RepositorioClientesArray.getInstance();
        IRepositorioVeiculos repoVeiculos = RepositorioVeiculosArray.getInstance();
        IRepositorioVendedores repoVendedores = RepositorioVendedoresArray.getInstance();
        IRepositorioOS repoOS = RepositorioOsArray.getInstance();
        IRepositorioTD repoTestDrive = RepositorioTestDriveArray.getInstance();
        
        // 🟢 REPO DOS MECÂNICOS
        IRepositorioMecanicos repoMecanicos = RepositorioMecanicosArray.getInstance();
        
        // Instanciação e inserção dos mecânicos Mario e Luigi no banco de dados da oficina
        if (repoMecanicos.procurarMecanico("Mario") == null) {
            repoMecanicos.adicionarMecanico(new Mecanico("Mario", true));
        }
        if (repoMecanicos.procurarMecanico("Luigi") == null) {
            repoMecanicos.adicionarMecanico(new Mecanico("Luigi", true));
        }
        
        // =========================================================================
        // 🟢 Passo 2: Instanciar os Gerenciadores Primeiro
        // =========================================================================
        // Correção de assinatura: repoVeiculos e repoVendedores invertidos para compilar
        IGerenciadorVenda gVenda = new GerenciadorVenda(repoVendas, repoClientes, repoVendedores, repoVeiculos);
        
        // Correção de assinatura: Usando o repoMecanicos em vez dos objetos mecânicos diretos
        IGerenciadorOficina gOficina = new GerenciadorOficina(repoOS, repoClientes, repoVeiculos, repoMecanicos);
        
        IGerenciadorRelatorio gRelatorio = new GerenciadorRelatorio(repoVendas, repoOS);
        IGerenciadorTestDrive gTestDrive = new GerenciadorTestDrive(repoTestDrive, repoClientes, repoVeiculos);
        
        // =========================================================================
        // 🟢 Passo 3: Casos de Teste Blocados (Vendas, Alertas e Relatórios)
        // =========================================================================
        
        // Dados Base comuns aos testes
        Cliente c1 = new Cliente("Samuel Silva", "123.456.789-00", "CNH12345", "samuel@email.com", "(81) 99999-9999");
        Cliente c2 = new Cliente("Maria Souza", "987.654.321-11", "CNH54321", "maria@email.com", "(81) 98888-8888");
        Cliente c3 = new Cliente("Yuri Neves", "321.122.567-12", "CNH1567", "yuri@gmail.com", "(81)9199-1919");
                
        // Evita duplicar registros se os dados já tiverem sido carregados do arquivo .dat
        if (repoClientes.procurarCliente("123.456.789-00") == null) {
            repoClientes.adicionarCliente(c1);
        }
        if (repoClientes.procurarCliente("987.654.321-11") == null) {
            repoClientes.adicionarCliente(c2);
        }
        if (repoClientes.procurarCliente("321.122.567-12") == null) {
            repoClientes.adicionarCliente(c3);
        }
        
        Vendedor vend1 = new Vendedor("Artur M.", 0.05);
        Vendedor vend2 = new Vendedor("Otavio R.", 0.05); 
        
        if (repoVendedores.procurarVendedor("Artur M.") == null) {
            repoVendedores.adicionarVendedor(vend1);
        }
        if (repoVendedores.procurarVendedor("Otavio R.") == null) {
            repoVendedores.adicionarVendedor(vend2);
        }
        
        // --- 3.1. CASOS PARA REALIZAR VENDA (Formulário da Interface) ---
        VeiculoNovo v1 = new VeiculoNovo("93X82KAA", "RENAVAM111", "Chevrolet Onix", 2026, 75000.00);
        if (repoVeiculos.procurarVeiculo("93X82KAA") == null) {
            repoVeiculos.adicionarVeiculo(v1); 
        }
        
        VeiculoSeminovo v2 = new VeiculoSeminovo("82J91PBB", "RENAVAM222", "Ford Ka", 2021, 45000.00, 5000.0);
        if (repoVeiculos.procurarVeiculo("82J91PBB") == null) {
            repoVeiculos.adicionarVeiculo(v2);
        }

        // --- 3.2. CASOS PARA VERIFICAR ALERTA ---
        VeiculoSeminovo vAlerta = new VeiculoSeminovo("CHASSIALERTA", "RENAVAM333", "Toyota Corolla", 2020, 90000.00, 15000.0);
        if (repoVeiculos.procurarVeiculo("CHASSIALERTA") == null) {
            repoVeiculos.adicionarVeiculo(vAlerta);
        }
        
        VeiculoSeminovo vSemAlerta = new VeiculoSeminovo("CHASSILIMPO", "RENAVAM444", "Fiat Uno", 2022, 30000.00, 2000.0);
        if (repoVeiculos.procurarVeiculo("CHASSILIMPO") == null) {
            repoVeiculos.adicionarVeiculo(vSemAlerta);
        }

        // --- 3.3. DADOS DE HISTÓRICO PRÉVIO PARA OS RELATÓRIOS E CONSULTAS ---
        VeiculoNovo carRelatorio1 = new VeiculoNovo("CHASSIREP1", "RENREP1", "Hyundai HB20", 2025, 80000.00);
        VeiculoNovo carRelatorio2 = new VeiculoNovo("CHASSIREP2", "RENREP2", "Jeep Renegade", 2024, 110000.00);
        VeiculoNovo carRelatorio3 = new VeiculoNovo("CHASSIREP3", "RENREP3", "Fiat Pulse", 2025, 95000.00);
        
        if (repoVeiculos.procurarVeiculo("CHASSIREP1") == null) repoVeiculos.adicionarVeiculo(carRelatorio1);
        if (repoVeiculos.procurarVeiculo("CHASSIREP2") == null) repoVeiculos.adicionarVeiculo(carRelatorio2);
        if (repoVeiculos.procurarVeiculo("CHASSIREP3") == null) repoVeiculos.adicionarVeiculo(carRelatorio3);
        
        // 🟢 [HISTÓRICO ATUALIZADO]: 3 Vendas com datas diferentes para testar filtros na tabela
        if (repoVendas.listarTodasVendas().isEmpty()) {
            // Venda 1: Realizada em Abril de 2026
            gVenda.efetuarVenda(0, "123.456.789-00", "CHASSIREP1", "Artur M.", 20000.00, 
                LocalDateTime.of(2026, 4, 15, 14, 30));
                
            // Venda 2: Realizada em Maio de 2026
            gVenda.efetuarVenda(0, "987.654.321-11", "CHASSIREP2", "Otavio R.", 35000.00, 
                LocalDateTime.of(2026, 5, 10, 10, 15));
                
            // Venda 3: Realizada em Junho de 2026 (Mês Atual)
            gVenda.efetuarVenda(0, "321.122.567-12", "CHASSIREP3", "Artur M.", 15000.00, 
                LocalDateTime.of(2026, 6, 02, 16, 45));
        }
        
     // --- 3.4. MASSA DE TESTES EXCLUSIVA PARA A OFICINA (YURI) ---
        // Garante que temos carros extras cadastrados usando a classe correta (VeiculoSeminovo)
        if (repoVeiculos.procurarVeiculo("CHASSI_FILA_1") == null) {
            repoVeiculos.adicionarVeiculo(new VeiculoSeminovo("CHASSI_FILA_1", "AAA1111", "Fiat Uno", 2015, 25000.0, 80000.0));
            repoVeiculos.adicionarVeiculo(new VeiculoSeminovo("CHASSI_FILA_2", "BBB2222", "Ford Ka", 2018, 35000.0, 50000.0));
            repoVeiculos.adicionarVeiculo(new VeiculoSeminovo("CHASSI_FILA_3", "CCC3333", "Chevrolet Onix", 2020, 55000.0, 30000.0));
        }

        // Executa a massa de testes de fila se o repositório estiver limpo
        if (repoOS.listarTodas().isEmpty()) {
            System.out.println("\n--- [TESTE YURI] Iniciando Simulação Automatizada de Fila ---");

            // 1. Ocupa o primeiro mecânico (Mario) abrindo a OS 1
            System.out.println("-> Abrindo OS 1 para o Veículo 1...");
            gOficina.abrirOS(c1.getCpf(), "CHASSI_FILA_1"); 

            // 2. Ocupa o segundo mecânico (Luigi) abrindo a OS 2
            System.out.println("-> Abrindo OS 2 para o Veículo 2...");
            gOficina.abrirOS(c1.getCpf(), "CHASSI_FILA_2"); 

            // 3. Tenta abrir a OS 3. Como Mario e Luigi estão ocupados, ela DEVE ir para a Fila (Status ABERTA)
            System.out.println("-> Abrindo OS 3 para o Veículo 3 (Não há mecânicos livres)...");
            gOficina.abrirOS(c1.getCpf(), "CHASSI_FILA_3"); 

            // Vamos listar para conferir o status em que elas nasceram em memória
            System.out.println("\n--- ESTADO DAS ORDENS LOGO APÓS ABERTURA ---");
            for (OrdemServico os : repoOS.listarTodas()) {
                System.out.println("OS Nº: " + os.getNumero() + 
                                   " | Status: " + os.getStatus() + 
                                   " | Mecânico: " + (os.getMecanico() != null ? os.getMecanico().getNome() : "NENHUM (Na Fila)"));
            }
            
            // 4. TESTE DO GATILHO: Vamos finalizar a primeira OS para liberar o Mario
            int numeroOS1 = repoOS.listarTodas().get(0).getNumero();
            
            System.out.println("\n-> [GATILHO] Finalizando a OS 1 (Nº " + numeroOS1 + ") para liberar o mecânico...");
            gOficina.finalizarServico(numeroOS1);

            System.out.println("\n--- ESTADO DAS ORDENS APÓS A LIBERAÇÃO DO MECÂNICO ---");
            for (OrdemServico os : repoOS.listarTodas()) {
                System.out.println("OS Nº: " + os.getNumero() + 
                                   " | Status: " + os.getStatus() + 
                                   " | Mecânico: " + (os.getMecanico() != null ? os.getMecanico().getNome() : "NENHUM"));
            }
            System.out.println("-------------------------------------------------------------\n");
        }

        VeiculoNovo vOficinaDisponivel = new VeiculoNovo("CHASSIOFICINA", "RENOF001", "Volkswagen Polo", 2026, 89000.00);
        if (repoVeiculos.procurarVeiculo("CHASSIOFICINA") == null) {
            repoVeiculos.adicionarVeiculo(vOficinaDisponivel);
        }
        System.out.println("-> [Main] Todos os erros corrigidos! Casos de teste integrados e persistência ativa.");
        
        // =========================================================================
        // 🟢 Passo 4: Configurar o palco principal e abrir a aplicação
        // =========================================================================
        ScreenManager.getInstance().setMainStage(primaryStage);
        ScreenManager.getInstance().injetarGerenciadoresNasTelas(gVenda, gOficina, gRelatorio, gTestDrive); 
        ScreenManager.getInstance().showMenuPrincipal();
    }
    
    public static void main(String[] args) {
        launch(args); 
    }
}
