package br.ufrpe.autodrive;

import br.ufrpe.autodrive.dados.*;
import br.ufrpe.autodrive.negocio.*;
import br.ufrpe.autodrive.negocio.beans.*; 
import br.ufrpe.autodrive.gui.ScreenManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Passo 1: Criar os repositórios em memória
        IRepositorioVendas repoVendas = new RepositorioVendasArray();
        IRepositorioClientes repoClientes = new RepositorioClientesArray();
        IRepositorioVeiculos repoVeiculos = new RepositorioVeiculosArray();
        IRepositorioVendedores repoVendedores = new RepositorioVendedoresArray();
        IRepositorioOS repoOS = new RepositorioOsArray();
        IRepositorioTD repoTestDrive = new RepositorioTestDriveArray();
        
        // =========================================================================
        // 🟢 Passo 2: Instanciar os Gerenciadores Primeiro (Necessário para a Opção A)
        // =========================================================================
        IGerenciadorVenda gVenda = new GerenciadorVenda(repoVendas, repoClientes, repoVendedores, repoVeiculos);
        IGerenciadorOficina gOficina = new GerenciadorOficina(repoOS, repoClientes, repoVeiculos);
        IGerenciadorRelatorio gRelatorio = new GerenciadorRelatorio(repoVendas, repoOS);
        IGerenciadorTestDrive gTestDrive = new GerenciadorTestDrive(repoTestDrive, repoClientes, repoVeiculos);
        
     // =========================================================================
        // 🟢 Passo 3: Casos de Teste Blocados (Vendas, Alertas e Relatórios)
        // =========================================================================
        
        // Dados Base comuns aos testes
        Cliente c1 = new Cliente("Samuel Silva", "123.456.789-00", "CNH12345", "samuel@email.com", "(81) 99999-9999");
        Cliente c2 = new Cliente("Maria Souza", "987.654.321-11", "CNH54321", "maria@email.com", "(81) 98888-8888");
        repoClientes.adicionarCliente(c1);
        repoClientes.adicionarCliente(c2);
        
        Vendedor vend1 = new Vendedor("Artur M.", 0.05);
        Vendedor vend2 = new Vendedor("Otavio R.", 0.05); 
        repoVendedores.adicionarVendedor(vend1);
        repoVendedores.adicionarVendedor(vend2);
        
        // --- 3.1. CASOS PARA REALIZAR VENDA (Formulário da Interface) ---
        VeiculoNovo v1 = new VeiculoNovo("93X82KAA", "RENAVAM111", "Chevrolet Onix", 2026, 75000.00);
        repoVeiculos.adicionarVeiculo(v1); 
        
        VeiculoSeminovo v2 = new VeiculoSeminovo("82J91PBB", "RENAVAM222", "Ford Ka", 2021, 45000.00, 5000.0);
        repoVeiculos.adicionarVeiculo(v2);

        // --- 3.2. CASOS PARA VERIFICAR ALERTA ---
        VeiculoSeminovo vAlerta = new VeiculoSeminovo("CHASSIALERTA", "RENAVAM333", "Toyota Corolla", 2020, 90000.00, 15000.0);
        repoVeiculos.adicionarVeiculo(vAlerta);
        
        VeiculoSeminovo vSemAlerta = new VeiculoSeminovo("CHASSILIMPO", "RENAVAM444", "Fiat Uno", 2022, 30000.00, 2000.0);
        repoVeiculos.adicionarVeiculo(vSemAlerta);

        // --- 3.3. DADOS DE HISTÓRICO PRÉVIO PARA OS RELATÓRIOS DO OTÁVIO ---
        // Adicionando veículos extras no repositório usando o método correto da interface
        VeiculoNovo carRelatorio1 = new VeiculoNovo("CHASSIREP1", "RENREP1", "Hyundai HB20", 2025, 80000.00);
        VeiculoNovo carRelatorio2 = new VeiculoNovo("CHASSIREP2", "RENREP2", "Jeep Renegade", 2024, 110000.00);
        VeiculoNovo carRelatorio3 = new VeiculoNovo("CHASSIREP3", "RENREP3", "Fiat Pulse", 2025, 95000.00);
        repoVeiculos.adicionarVeiculo(carRelatorio1);
        repoVeiculos.adicionarVeiculo(carRelatorio2);
        repoVeiculos.adicionarVeiculo(carRelatorio3);
        
        // 🟢 AGORA USANDO A NOVA SOBRECARGA: As datas são passadas diretamente na criação!
        gVenda.efetuarVenda(501, "123.456.789-00", "CHASSIREP1", "Artur M.", 20000.00, 
            java.time.LocalDateTime.of(2026, 5, 10, 14, 30));
            
        gVenda.efetuarVenda(502, "987.654.321-11", "CHASSIREP2", "Otavio R.", 35000.00, 
            java.time.LocalDateTime.of(2026, 5, 20, 10, 15));
            
        gVenda.efetuarVenda(503, "123.456.789-00", "CHASSIREP3", "Artur M.", 15000.00, 
            java.time.LocalDateTime.of(2026, 5, 20, 16, 45));
        
        // --- 3.4. MASSA DE TESTES EXCLUSIVA PARA A OFICINA (YURI) ---

        Mecanico mecanicoOficina = new Mecanico("Pedro Mecânico", 500.0, true);

        // 1. Instanciando a Ordem de Serviço Aberta
        OrdemServico os1 = new OrdemServico(901, "31/05/2026", c1, vAlerta);

        // 2. Adicionando Peças - CORRIGIDO: O nome DEVE ser exatamente "oleo" para passar no validarItensObrigatorios()
        Pecas peca1 = new Pecas();
        peca1.setNome("oleo"); // 🟢 Antes estava "Oleo Motor" e o sistema barrava!
        peca1.setPreco(250.00);
        peca1.setQuantidade(1);
        os1.getListaPecas().add(peca1);

        // 3. Adicionando Mão de Obra
        MaoDeObra servico1 = new MaoDeObra();
        servico1.setDescricao("Troca de Pastilhas");
        servico1.setValor(150.00);
        servico1.setHoras(2.0);
        servico1.setMecanico(mecanicoOficina);
        os1.getListaServicos().add(servico1);

        // 4. CORRIGIDO: Forçando o pagamento da OS para cumprir a Regra 1
        os1.marcarComoPago(); // 🟢 Agora o status passa para PAGA e permite finalizar!

        // 5. Salvando no repositório de OS
        repoOS.salvar(os1);

        // Caso de teste auxiliar para a Abertura de OS na Interface
        VeiculoNovo vOficinaDisponivel = new VeiculoNovo("CHASSIOFICINA", "RENOF001", "Volkswagen Polo", 2026, 89000.00);
        repoVeiculos.adicionarVeiculo(vOficinaDisponivel);

        System.out.println("-> [Main] Todos os erros corrigidos! Casos de teste integrados e prontos.");
        
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
