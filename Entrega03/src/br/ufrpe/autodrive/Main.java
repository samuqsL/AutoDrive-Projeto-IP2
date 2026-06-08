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
        // =========================================================================
        // 1: Inicialização dos Repositórios (Singleton + Persistência)
        // =========================================================================
        IRepositorioVendas repoVendas = RepositorioVendasArray.getInstance();
        IRepositorioClientes repoClientes = RepositorioClientesArray.getInstance();
        IRepositorioVeiculos repoVeiculos = RepositorioVeiculosArray.getInstance();
        IRepositorioVendedores repoVendedores = RepositorioVendedoresArray.getInstance();
        IRepositorioOS repoOS = RepositorioOsArray.getInstance();
        IRepositorioTD repoTestDrive = RepositorioTestDriveArray.getInstance();
        
        // NOVO: Repositório de mecânicos injetado
        IRepositorioMecanicos repoMecanicos = RepositorioMecanicosArray.getInstance();
        
        // =========================================================================
        // 2: Instanciar os Mecânicos Individuais, Salvar e criar os Gerenciadores
        // =========================================================================
        
        // Se a lista estiver vazia (primeira execução), adiciona os mecânicos no repositório
        if (repoMecanicos.listarTodos().isEmpty()) {
            Mecanico mario = new Mecanico("Mario", true);
            Mecanico luigi = new Mecanico("Luigi", true);
            repoMecanicos.adicionarMecanico(mario);
            repoMecanicos.adicionarMecanico(luigi);
        }
        
        // Correções nas assinaturas (Respeitando os tipos de Repositórios)
        IGerenciadorVenda gVenda = new GerenciadorVenda(repoVendas, repoClientes, repoVeiculos, repoVendedores);
        
        // Acoplamento correto do Repositório de Mecânicos no Gerenciador de Oficina
        IGerenciadorOficina gOficina = new GerenciadorOficina(repoOS, repoClientes, repoVeiculos, repoMecanicos);
        
        IGerenciadorTestDrive gTestDrive = new GerenciadorTestDrive(repoTestDrive, repoClientes, repoVeiculos, repoVendedores);
        
        System.out.println("-> [Main] Oficina configurada. Fila reativa operacional!");
        
        // =========================================================================
        // 3: Configurar o palco principal do JavaFX e abrir a aplicação
        // =========================================================================
        ScreenManager.getInstance().setMainStage(primaryStage);
        
        // Correção: Passando exatamente os gerenciadores que as telas esperam
        ScreenManager.getInstance().injetarGerenciadoresNasTelas(gVenda, gOficina, gTestDrive);
        ScreenManager.getInstance().showMenuPrincipal();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
