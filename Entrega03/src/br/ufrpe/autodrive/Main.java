package br.ufrpe.autodrive;

import br.ufrpe.autodrive.dados.*;
import br.ufrpe.autodrive.negocio.*;
import br.ufrpe.autodrive.negocio.beans.*; 
import br.ufrpe.autodrive.gui.ScreenManager;
import javafx.application.Application;
import javafx.stage.Stage;
import java.time.LocalDateTime;

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
        IGerenciadorVenda gVenda = new GerenciadorVenda(repoVendas, repoClientes, repoVeiculos, repoVendedores);
        
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
        // Altera para buscar direto do banco de dados o Mario para a OS de teste prévia
        Mecanico mecanicoOficina = repoMecanicos.procurarMecanico("Mario");

        // Proteção de persistência: só insere a OS se o banco de dados de OS estiver vazio
        if (repoOS.listarTodas().isEmpty()) {
            
            // 🟢 CORREÇÃO: Usando o novo construtor automatizado do Yuri (Sem número e sem data manual)
            OrdemServico os1 = new OrdemServico(c1, vAlerta);

            Pecas peca1 = new Pecas();
            peca1.setNome("oleo"); 
            peca1.setPreco(250.00);
            peca1.setQuantidade(1);
            os1.getListaPecas().add(peca1);

            MaoDeObra servico1 = new MaoDeObra();
            servico1.setDescricao("Troca de Pastilhas");
            servico1.setValor(150.00);
            servico1.setHoras(2.0);
            servico1.setMecanico(mecanicoOficina);
            os1.getListaServicos().add(servico1);

            // Simula o fluxo de encerramento correto
            os1.setStatus(StatusOS.FINALIZADA); 
            
            // Salva no repositório persistente (Singleton vai gravar no arquivo .dat automaticamente)
            repoOS.salvar(os1);
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
