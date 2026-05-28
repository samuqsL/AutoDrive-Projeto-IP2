package br.ufrpe.autodrive.gui;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import br.ufrpe.autodrive.negocio.*; // Importa suas interfaces de negócio

public class ScreenManager {
    
    private static ScreenManager instance;
    private Stage mainStage;
    
    // 1. AS CENAS (O "canal" que vai aparecer na TV)
    private Scene cenaMenuPrincipal;
    private Scene cenaVenda;
    private Scene cenaOficina;
    private Scene cenaRelatorio;
    private Scene cenaTestDrive;
    
    // 2. OS CONTROLLERS (Para conseguirmos injetar os gerenciadores neles depois)
    private MenuPrincipal controllerMenu;
    private TelaVenda controllerVenda;
    // private TelaOficina controllerOficina; // Descomente quando a classe existir
    // private TelaRelatorio controllerRelatorio; // Descomente quando a classe existir
    // private TelaTestDrive controllerTestDrive; // Descomente quando a classe existir

    // Padrão Singleton do ScreenManager
    public static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        } 
        return instance; 
    }
    
    // Construtor privado: Carrega TODOS os FXMLs uma única vez ao iniciar o sistema
    private ScreenManager() {
        try {
            // A. Carrega Menu Principal
            FXMLLoader loaderMenu = new FXMLLoader(getClass().getResource("/fxml/MenuPrincipal.fxml"));
            this.cenaMenuPrincipal = new Scene(loaderMenu.load());
            this.controllerMenu = loaderMenu.getController();

            // B. Carrega Tela de Vendas (Samuel)
            FXMLLoader loaderVenda = new FXMLLoader(getClass().getResource("/fxml/TelaVenda.fxml"));
            this.cenaVenda = new Scene(loaderVenda.load());
            this.controllerVenda = loaderVenda.getController();

            /* // C. Carrega Tela de Oficina (Yuri) - Descomente quando criarem o FXML
            FXMLLoader loaderOficina = new FXMLLoader(getClass().getResource("/fxml/TelaOficina.fxml"));
            this.cenaOficina = new Scene(loaderOficina.load());
            this.controllerOficina = loaderOficina.getController();

            // D. Carrega Tela de Relatórios (Otávio) - Descomente quando criarem o FXML
            FXMLLoader loaderRelatorio = new FXMLLoader(getClass().getResource("/fxml/TelaRelatorio.fxml"));
            this.cenaRelatorio = new Scene(loaderRelatorio.load());
            this.controllerRelatorio = loaderRelatorio.getController();

            // E. Carrega Tela de Test Drive (Artur) - Descomente quando criarem o FXML
            FXMLLoader loaderTD = new FXMLLoader(getClass().getResource("/fxml/TelaTestDrive.fxml"));
            this.cenaTestDrive = new Scene(loaderTD.load());
            this.controllerTestDrive = loaderTD.getController();
            */
            
        } catch (IOException e) {
            System.out.println("❌ [ScreenManager] Erro crítico ao carregar arquivos FXML. Verifique os caminhos e os fx:controller!");
            e.printStackTrace();
        }
    }

    // Configura o palco principal que veio lá da classe Main
    public void setMainStage(Stage stage) {
        this.mainStage = stage;
        this.mainStage.setWidth(1024);
        this.mainStage.setHeight(768);
        this.mainStage.setResizable(false); // Impede o usuário de bagunçar o tamanho da tela
        this.mainStage.setTitle("AutoDrive - Sistema de Gerenciamento Concessionária");
    }
    
    /**
     * MÉTODO CRUCIAL: Recebe os gerenciadores criados na Main e distribui 
     * para cada tela correspondente antes do sistema começar a navegar.
     */
    public void injetarGerenciadoresNasTelas(IGerenciadorVenda gV, IGerenciadorOficina gO, IGerenciadorRelatorio gR, IGerenciadorTestDrive gT) {
        // Injeta no Menu Principal (Caso ele precise repassar algo)
        if (this.controllerMenu != null) {
            this.controllerMenu.injetarGerenciadores(gV, gO, gR, gT);
        }
        // Injeta direto na tela de vendas do Samuel
        if (this.controllerVenda != null) {
            this.controllerVenda.injetarGerenciador(gV);
        }
        /*
        // Injeta na oficina do Yuri
        if (this.controllerOficina != null) {
            this.controllerOficina.injetarGerenciador(gO);
        }
        // Injeta nos relatórios do Otávio
        if (this.controllerRelatorio != null) {
            this.controllerRelatorio.injetarGerenciador(gR);
        }
        // Injeta no test drive do Artur
        if (this.controllerTestDrive != null) {
            this.controllerTestDrive.injetarGerenciador(gT);
        }
        */
        System.out.println("-> [ScreenManager] Todos os gerenciadores de negócio foram distribuídos com sucesso!");
    }
    
    // 3. MÉTODOS DE NAVEGAÇÃO (Trocam o "canal" na mesma janela física)
    
    public void showMenuPrincipal() {
        this.mainStage.setScene(this.cenaMenuPrincipal);
        this.mainStage.show();
    }
    
    public void showTelaVenda() {
        this.mainStage.setScene(this.cenaVenda);
        this.mainStage.show();
    }

    public void showTelaOficina() {
        this.mainStage.setScene(this.cenaOficina);
        this.mainStage.show();
    }

    public void showTelaRelatorio() {
        this.mainStage.setScene(this.cenaRelatorio);
        this.mainStage.show();
    }

    public void showTelaTestDrive() {
        this.mainStage.setScene(this.cenaTestDrive);
        this.mainStage.show();
    }
}
