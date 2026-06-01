package br.ufrpe.autodrive.gui;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import br.ufrpe.autodrive.negocio.*; // Importa as interfaces dos seus gerenciadores

public class ScreenManager {
    
    private static ScreenManager instance;
    private Stage mainStage;
    
    // 1. AS CENAS (Cada "canal" que vai aparecer na tela principal)
    private Scene cenaMenuPrincipal;
    private Scene cenaVenda;
    private Scene cenaOficina;
    private Scene cenaRelatorio;
    private Scene cenaTestDrive;
    
    // 2. OS CONTROLLERS (Guardamos a referência para injetar as regras de negócio neles)
    private MenuPrincipal controllerMenu;
    private TelaVenda controllerVenda;
    private TelaOficina controllerOficina;
    private TelaRelatorio controllerRelatorio;
    private TelaTestDrive controllerTestDrive;

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
            // A. Carrega o Menu Principal
            FXMLLoader loaderMenu = new FXMLLoader(getClass().getResource("/fxml/MenuPrincipal.fxml"));
            this.cenaMenuPrincipal = new Scene(loaderMenu.load());
            this.controllerMenu = loaderMenu.getController();

            // B. Carrega a Tela de Vendas (Samuel)
            FXMLLoader loaderVenda = new FXMLLoader(getClass().getResource("/fxml/TelaVenda.fxml"));
            this.cenaVenda = new Scene(loaderVenda.load());
            this.controllerVenda = loaderVenda.getController();

            // C. Carrega Tela de Oficina (Yuri) - Descomente quando criarem o FXML e o controller
            FXMLLoader loaderOficina = new FXMLLoader(getClass().getResource("/fxml/TelaOficina.fxml"));
            this.cenaOficina = new Scene(loaderOficina.load());
            this.controllerOficina = loaderOficina.getController();
		
            
            // D. Carrega Tela de Relatórios (Otávio)
            FXMLLoader loaderRelatorio = new FXMLLoader(getClass().getResource("/fxml/TelaRelatorio.fxml"));
            this.cenaRelatorio = new Scene(loaderRelatorio.load());
            this.controllerRelatorio = loaderRelatorio.getController();
            
		
            // E. Carrega Tela de Test Drive (Artur) - Descomente quando criarem o FXML e o controller
            FXMLLoader loaderTD = new FXMLLoader(getClass().getResource("/fxml/TelaTestDrive.fxml"));
            this.cenaTestDrive = new Scene(loaderTD.load());
            this.controllerTestDrive = loaderTD.getController();
            
        } catch (IOException e) {
            System.out.println("❌ [ScreenManager] Erro crítico ao carregar arquivos FXML. Verifique caminhos ou nomes!");
            e.printStackTrace();
        }
    }

    // Configura as propriedades do palco único do sistema operacional
    public void setMainStage(Stage stage) {
        this.mainStage = stage;
        this.mainStage.setWidth(1024);
        this.mainStage.setHeight(768);
        this.mainStage.setResizable(false); // Mantém o tamanho fixo profissional
        this.mainStage.setTitle("AutoDrive - Sistema de Gerenciamento de Concessionária");
    }
    
    /**
     * Esse método distribui os gerenciadores criados na sua Main 
     * para cada controller da tela de destino correspondente.
     */
    public void injetarGerenciadoresNasTelas(IGerenciadorVenda gV, IGerenciadorOficina gO, IGerenciadorRelatorio gR, IGerenciadorTestDrive gT) {
        // Injeta no Menu Principal
        if (this.controllerMenu != null) {
            this.controllerMenu.injetarGerenciadores(gV, gO, gR, gT);
        }
        // Injeta direto na tela de vendas do Samuel
        if (this.controllerVenda != null) {
            this.controllerVenda.injetarGerenciador(gV);
        }
        
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
        
        System.out.println("-> [ScreenManager] Gerenciadores injetados com sucesso nos Controllers!");
    }
    
    // 3. MÉTODOS DE TRANSIÇÃO (Chamados pelos botões para alternar as telas)
    
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
