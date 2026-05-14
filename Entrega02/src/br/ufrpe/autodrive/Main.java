package br.ufrpe.autodrive;

import br.ufrpe.autodrive.dados.*;
import br.ufrpe.autodrive.gui.MenuPrincipal;
import br.ufrpe.autodrive.negocio.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 1. Instanciar os Repositórios (Persistência)
        IRepositorioVendas repoVendas = new RepositorioVendas(); // Supondo que o nome seja este
        IRepositorioOS repoOS = new RepositorioOS();
        // Adicione outros repositórios conforme sua necessidade (Clientes, Veiculos, etc)

        // 2. Instanciar os Gerenciadores (Negócio)
        // Passamos os repositórios para que os gerenciadores saibam onde salvar/buscar
        IGerenciadorVenda gVenda = new GerenciadorVenda(repoVendas);
        IGerenciadorOficina gOficina = new GerenciadorOficina(repoOS);
        IGerenciadorRelatorio gRelatorio = new GerenciadorRelatorio(repoVendas, repoOS);
        IGerenciadorTestDrive gTestDrive = new GerenciadorTestDrive(); 

        // 3. Instanciar o Menu Principal (GUI)
        // O Menu recebe todos os gerenciadores para distribuir para as telas filhas
        MenuPrincipal menu = new MenuPrincipal(gVenda, gOficina, gRelatorio, gTestDrive);

        // 4. Iniciar o Sistema
        System.out.println("SISTEMA AUTO DRIVE INICIALIZADO COM SUCESSO!");
        menu.exibirMenu();
        
        System.out.println("Sistema encerrado. Até logo!");
    }
}
