package br.ufrpe.autodrive;

import br.ufrpe.autodrive.dados.*;
import br.ufrpe.autodrive.negocio.*;
import br.ufrpe.autodrive.negocio.beans.*; // Importa Cliente, Veiculo, Vendedor...
import br.ufrpe.autodrive.gui.ScreenManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    // 🟢 O MÉTODO START É O VERDADEIRO INICIALIZADOR DO SEU SISTEMA VISUAL
    @Override
    public void start(Stage primaryStage) {
        // Passo 1: Criar os repositórios normais em memória
        IRepositorioVendas repoVendas = new RepositorioVendasArray();
        IRepositorioClientes repoClientes = new RepositorioClientesArray();
        IRepositorioVeiculos repoVeiculos = new RepositorioVeiculosArray();
        IRepositorioVendedores repoVendedores = new RepositorioVendedoresArray();
        IRepositorioOS repoOS = new RepositorioOsArray();
        IRepositorioTD repoTestDrive = new RepositorioTestDriveArray();
        
        // Passo 2: Instanciar e guardar os objetos de teste (Popular as listas)
        Cliente c1 = new Cliente("123.456.789-00", "Samuel Silva");
        repoClientes.adicionarCliente(c1);
        
        Veiculo v1 = new Veiculo("93X82KAA", "Chevrolet Onix", 75000.00);
        repoVeiculos.adicionarVeiculo(v1); 
        
        Vendedor vend1 = new Vendedor("Artur M.", "111.222.333-44");
        repoVendedores.adicionarVendedor(vend1);
        
        System.out.println("-> [Main] Objetos de teste adicionados com sucesso antes das telas abrirem!");

        // Passo 3: Criar os Gerenciadores passando as listas preenchidas
        IGerenciadorVenda gVenda = new GerenciadorVenda(repoVendas, repoClientes, repoVendedores, repoVeiculos);
        IGerenciadorOficina gOficina = new GerenciadorOficina(repoOS, repoVeiculos, repoClientes);
        IGerenciadorRelatorio gRelatorio = new GerenciadorRelatorio(repoVendas);
        IGerenciadorTestDrive gTestDrive = new GerenciadorTestDrive(repoTestDrive, repoVeiculos, repoClientes);
        
        // Passo 4: Configurar o palco principal na central de telas (ScreenManager)
        ScreenManager.getInstance().setMainStage(primaryStage);
        
        // Passo 5: Injetar as regras de negócio em todas as telas carregadas
        ScreenManager.getInstance().injetarGerenciadoresNasTelas(gVenda, gOficina, gRelatorio, gTestDrive); 
        
        // Passo 6: Mostrar o Menu Principal dentro da janela de 1024x768
        ScreenManager.getInstance().showMenuPrincipal();
    }

    // ⚠️ O MÉTODO MAIN APENAS DA A PARTIDA NO MOTOR GRÁFICO
    public static void main(String[] args) {
        launch(args); // <- Ele congela aqui, chama o start() lá de cima, e só descongela quando o app fechar!
    }
}
