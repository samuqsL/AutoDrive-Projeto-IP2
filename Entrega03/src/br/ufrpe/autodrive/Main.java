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
        // 🟢 Passo 3: Casos de Teste Blocados (Vendas e Alertas)
        // =========================================================================
        
        // Dados Base comuns aos testes
        Cliente c1 = new Cliente("Samuel Silva", "123.456.789-00", "CNH12345", "samuel@email.com", "(81) 99999-9999");
        Cliente c2 = new Cliente("Maria Souza", "987.654.321-11", "CNH54321", "maria@email.com", "(81) 98888-8888");
        repoClientes.adicionarCliente(c1);
        repoClientes.adicionarCliente(c2);
        
        Vendedor vend1 = new Vendedor("Artur M.", 0.05);
        repoVendedores.adicionarVendedor(vend1);
        
        // --- 3.1. CASOS PARA REALIZAR VENDA (Formulário da Interface) ---
        
        // Caso Venda Válida: Carro em estoque pronto para ser comprado na GUI
        VeiculoNovo v1 = new VeiculoNovo("93X82KAA", "RENAVAM111", "Chevrolet Onix", 2026, 75000.00);
        repoVeiculos.adicionarVeiculo(v1); 
        
        // Caso Venda Inválida: Carro que simulará uma falha de validação na GUI
        VeiculoSeminovo v2 = new VeiculoSeminovo("82J91PBB", "RENAVAM222", "Ford Ka", 2021, 45000.00, 5000.0);
        repoVeiculos.adicionarVeiculo(v2);

        // --- 3.2. CASOS PARA VERIFICAR ALERTA (Histórico Gerado via Controller) ---

        // Alerta Positivo: Carro cadastrado com alta quilometragem
        VeiculoSeminovo vAlerta = new VeiculoSeminovo("CHASSIALERTA", "RENAVAM333", "Toyota Corolla", 2020, 90000.00, 15000.0);
        repoVeiculos.adicionarVeiculo(vAlerta);
        
        // Alerta Negativo: Carro cadastrado com baixa quilometragem
        VeiculoSeminovo vSemAlerta = new VeiculoSeminovo("CHASSILIMPO", "RENAVAM444", "Fiat Uno", 2022, 30000.00, 2000.0);
        repoVeiculos.adicionarVeiculo(vSemAlerta);

        System.out.println("-> [Main] Casos de teste configurados sem erros de compilação ou execução!");
        
        // =========================================================================
        // 🟢 Passo 4: Configurar o palco principal e abrir a aplicação
        // =========================================================================
        
        // Configurar o palco principal na central de telas (ScreenManager)
        ScreenManager.getInstance().setMainStage(primaryStage);
        
        // Injetar os gerenciadores de negócio em todas as telas
        ScreenManager.getInstance().injetarGerenciadoresNasTelas(gVenda, gOficina, gRelatorio, gTestDrive); 
        
        // Exibe a tela inicial do Menu Principal
        ScreenManager.getInstance().showMenuPrincipal();
    }
    
    public static void main(String[] args) {
        launch(args); 
    }
}
