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
        // 🟢 Passo 1: Inicialização dos Repositórios (Singleton + Persistência)
        // =========================================================================
        IRepositorioVendas repoVendas = RepositorioVendasArray.getInstance();
        IRepositorioClientes repoClientes = RepositorioClientesArray.getInstance();
        IRepositorioVeiculos repoVeiculos = RepositorioVeiculosArray.getInstance();
        IRepositorioVendedores repoVendedores = RepositorioVendedoresArray.getInstance();
        IRepositorioOS repoOS = RepositorioOsArray.getInstance();
        IRepositorioTD repoTestDrive = RepositorioTestDriveArray.getInstance();
        
        // =========================================================================
        // 🟢 Passo 2: Instanciar os Mecânicos Individuais e os Gerenciadores
        // =========================================================================
        
        // FUNÇÃO LOCALIZADA: Criação dos 2 mecânicos solicitados com produtividade individual ativa
        Mecanico mario = new Mecanico("Mario", true);
        Mecanico luigi = new Mecanico("Luigi", true);
        
        IGerenciadorVenda gVenda = new GerenciadorVenda(repoVendas, repoClientes, repoVeiculos, repoVendedores);
        
        // FUNÇÃO LOCALIZADA: Acoplamento de Mario e Luigi no Gerenciador de Oficina
        IGerenciadorOficina gOficina = new GerenciadorOficina(repoOS, repoClientes, repoVeiculos, mario, luigi);
        
        IGerenciadorTestDrive gTestDrive = new GerenciadorTestDrive(repoTestDrive, repoClientes, repoVeiculos, repoVendedores);
        
        System.out.println("-> [Main] Oficina configurada com Mario e Luigi. Fila reativa operacional!");
        
        // =========================================================================
        // 🟢 Passo 3: Configurar o palco principal do JavaFX e abrir a aplicação
        // =========================================================================
        ScreenManager.getInstance().setMainStage(primaryStage);
        ScreenManager.getInstance().injetarGerenciadoresNasTelas(gVenda, gOficina, gTestDrive);
        ScreenManager.getInstance().showMenuPrincipal();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
