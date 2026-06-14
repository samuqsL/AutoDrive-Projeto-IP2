package br.ufrpe.autodrive.gui;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import br.ufrpe.autodrive.negocio.*; // Importa as interfaces dos gerenciadores

public class ScreenManager {
    
    private static ScreenManager instance;
    private Stage mainStage;
    
    // 1. AS CENAS
    private Scene cenaMenuPrincipal;
    private Scene cenaVenda;
    private Scene cenaOficina;
    private Scene cenaRelatorio;
    private Scene cenaTestDrive;
    private Scene cenaCadastro; // <-- NOVO: cena da Tela de Cadastro!
    private Scene cenaEstoquePecas; // 💡 NOVO: Cena para o estoque de peças
    
    // 2. OS CONTROLLERS
    private MenuPrincipal controllerMenu;
    private TelaVenda controllerVenda;
    private TelaOficina controllerOficina;
    private TelaRelatorio controllerRelatorio;
    private TelaTestDrive controllerTestDrive;
    private TelaCadastro controllerCadastro; // <-- NOVO: ScreenManager agora possui atributo da TelaCadastro[controller]!
    private TelaEstoquePecas controllerEstoquePecas; // 💡 NOVO: Controller para o estoque de peças

    // Padrão Singleton
    public static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        } 
        return instance; 
    }
    
    // Construtor privado
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

            // C. Carrega Tela de Oficina (Yuri)
            FXMLLoader loaderOficina = new FXMLLoader(getClass().getResource("/fxml/TelaOficina.fxml"));
            this.cenaOficina = new Scene(loaderOficina.load());
            this.controllerOficina = loaderOficina.getController();
        
            // D. Carrega Tela de Relatórios (Otávio)
            FXMLLoader loaderRelatorio = new FXMLLoader(getClass().getResource("/fxml/TelaRelatorio.fxml"));
            this.cenaRelatorio = new Scene(loaderRelatorio.load());
            this.controllerRelatorio = loaderRelatorio.getController();
            
            // E. Carrega Tela de Test Drive (Artur)
            FXMLLoader loaderTD = new FXMLLoader(getClass().getResource("/fxml/TelaTestDrive.fxml"));
            this.cenaTestDrive = new Scene(loaderTD.load());
            this.controllerTestDrive = loaderTD.getController();
            
            // F. Carrega a NOVA Tela de Cadastro (Clientes e Veiculos)!
            // CORRIGIDO: O bloco abaixo foi totalmente ativado (retirado o comentário de bloco)
            FXMLLoader loaderCadastro = new FXMLLoader(getClass().getResource("/fxml/TelaCadastro.fxml"));
            this.cenaCadastro = new Scene(loaderCadastro.load());
            this.controllerCadastro = loaderCadastro.getController();
            
            // G. Carrega a Tela de Estoque de Peças do Yuri
            FXMLLoader loaderEstoque = new FXMLLoader(getClass().getResource("/fxml/TelaEstoquePecas.fxml"));
            this.cenaEstoquePecas = new Scene(loaderEstoque.load());
            this.controllerEstoquePecas = loaderEstoque.getController();
            
        } catch (IOException e) {
            System.out.println("❌ [ScreenManager] Erro crítico ao carregar arquivos FXML. Verifique caminhos ou nomes!");
            e.printStackTrace();
        }
    }

    public void setMainStage(Stage stage) {
        this.mainStage = stage;
        this.mainStage.setWidth(1024);
        this.mainStage.setHeight(768);
        this.mainStage.setResizable(false); 
        this.mainStage.setTitle("AutoDrive - Sistema de Gerenciamento de Concessionária");
    }
    
    //NOVO: Metodo de Injeção de Gerenciadores possui novo parametro --> "IGerenciadorCadastro gC"!
    public void injetarGerenciadores(IGerenciadorVenda gV, IGerenciadorOficina gO, IGerenciadorRelatorio gR, IGerenciadorTestDrive gT, IGerenciadorCadastro gC, IGerenciadorEstoquePecas gEstoque) {
        // Injeta no Menu Principal
        if (this.controllerMenu != null) {
            this.controllerMenu.injetarGerenciadores(gV, gO, gR, gT, gC, gEstoque);
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
        
        // NOVO: Injeta gerenciador correspondente na tela de Cadastro!
        if (this.controllerCadastro != null) {
            this.controllerCadastro.injetarGerenciador(gC);
        }
        
        // 💡 NOVO: Injeta o gerenciador de estoque diretamente na tela correspondente
        if (this.controllerEstoquePecas != null) {
            this.controllerEstoquePecas.injetarGerenciador(gEstoque);
        }
        
        System.out.println("-> [ScreenManager] Gerenciadores injetados com sucesso nos Controllers!(Incluindo ESTOQUE");
    }
    
    // 3. MÉTODOS DE TRANSIÇÃO
    
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
    
    // NOVO: Ativado o método de transição para que o Menu Principal consiga chamá-lo (TelaCadastro)*
    public void showTelaCadastro() {
    	// Antes de mudar a cena, avisa ao controller para se atualizar!
        if (this.controllerCadastro != null) {
            this.controllerCadastro.aoExibirTela();
        }
        
        this.mainStage.setScene(this.cenaCadastro);
        this.mainStage.show();
    }
    
    // 💡 NOVO: Ativa a transição para exibir a tela de gerenciamento de estoque de peças
    public void showTelaEstoquePecas() {
        this.mainStage.setScene(this.cenaEstoquePecas);
        this.mainStage.show();
    }
}
