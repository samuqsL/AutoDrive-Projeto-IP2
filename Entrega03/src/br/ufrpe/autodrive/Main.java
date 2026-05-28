package br.ufrpe.autodrive;

import br.ufrpe.autodrive.dados.*;
import br.ufrpe.autodrive.negocio.*;
import br.ufrpe.autodrive.negocio.beans.*; // Garante o import das tuas classes (Cliente, VeiculoNovo, etc.)
import br.ufrpe.autodrive.gui.ScreenManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Passo 1: Criar os repositórios normais em memória
        IRepositorioVendas repoVendas = new RepositorioVendasArray();
        IRepositorioClientes repoClientes = new RepositorioClientesArray();
        IRepositorioVeiculos repoVeiculos = new RepositorioVeiculosArray();
        IRepositorioVendedores repoVendedores = new RepositorioVendedoresArray();
        IRepositorioOS repoOS = new RepositorioOsArray();
        IRepositorioTD repoTestDrive = new RepositorioTestDriveArray();
        
        // =========================================================================
        // 🟢 Passo 2: Instanciar objetos de teste (Casando com os seus construtores reais)
        // =========================================================================
        
        // 1. Cliente: Construtor real espera (nome, cpf, cnh, email, telefone)
        Cliente c1 = new Cliente("Samuel Silva", "123.456.789-00", "CNH12345", "samuel@email.com", "(81) 99999-9999");
        repoClientes.adicionarCliente(c1);
        
        // 2. Vendedor: Construtor real espera (nome, percentualComissao)
        Vendedor vend1 = new Vendedor("Artur M.", 0.05); // 5% de comissão
        repoVendedores.adicionarVendedor(vend1);
        
        // 3. Veículo Novo: O seu construtor real espera exatamente 5 parâmetros: (chassi, renavam, modelo, ano, preco)
        VeiculoNovo v1 = new VeiculoNovo("93X82KAA", "RENAVAM111", "Chevrolet Onix", 2026, 75000.00);
        repoVeiculos.adicionarVeiculo(v1); 
        
        // 4. Veículo Seminovo: O seu construtor real espera 6 parâmetros: (chassi, renavam, modelo, ano, preco, quilometragem)
        VeiculoSeminovo v2 = new VeiculoSeminovo("82J91PBB", "RENAVAM222", "Ford Ka", 2021, 45000.00, 32000.0);
        repoVeiculos.adicionarVeiculo(v2);

        System.out.println("-> [Main] Objetos de teste adicionados com sucesso!");

        // =========================================================================
        // 🟢 Passo 3: Criar os Gerenciadores (Ajustado com as assinaturas reais das classes)
        // =========================================================================
        IGerenciadorVenda gVenda = new GerenciadorVenda(repoVendas, repoClientes, repoVendedores, repoVeiculos);
        IGerenciadorOficina gOficina = new GerenciadorOficina(repoOS, repoClientes, repoVeiculos);
        IGerenciadorRelatorio gRelatorio = new GerenciadorRelatorio(repoVendas, repoOS);
        IGerenciadorTestDrive gTestDrive = new GerenciadorTestDrive(repoTestDrive, repoClientes, repoVeiculos);
        
        // =========================================================================
        // 🟢 LINHAS QUE FALTAVAM: Enviar os dados para o ScreenManager e abrir o App
        // =========================================================================
        
        // Passo 4: Configurar o palco principal na central de telas (ScreenManager)
        ScreenManager.getInstance().setMainStage(primaryStage);
        
        // Passo 5: Injetar as regras de negócio em todas as telas carregadas
        // (Isso faz o Java "usar" as variáveis gOficina, gRelatorio e gTestDrive, sumindo o aviso amarelo!)
        ScreenManager.getInstance().injetarGerenciadoresNasTelas(gVenda, gOficina, gRelatorio, gTestDrive); 
        
        // Passo 6: Chamar a tela inicial do Menu Principal para abrir na tela do Windows
        ScreenManager.getInstance().showMenuPrincipal();
    }
    
    public static void main(String[] args) {
        launch(args); 
    }
}
