package br.ufrpe.autodrive;

import br.ufrpe.autodrive.dados.*;
import br.ufrpe.autodrive.gui.MenuPrincipal;
import br.ufrpe.autodrive.negocio.*;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
	    // 1. REPOSITÓRIOS (Nomes exatos das classes de dados)
	    IRepositorioVendas rv = new RepositorioVendasArray(); 
	    IRepositorioOS rs = new RepositorioOsArray();
	    IRepositorioClientes rc = new RepositorioClientesArray();
	    IRepositorioVendedores rVend = new RepositorioVendedoresArray();
	    IRepositorioVeiculos rVeic = new RepositorioVeiculosArray();
	    IRepositorioTD rTd = new RepositorioTestDriveArray();
	    
	    //2. Instancias de Cliente, Veiculo, Vendedor, Mecanico etc (Fundamentais para Telas/Gerenciadores)
	    /*
	    *
	    *                          (ADICIONAR)
	    *
	    */

	    // 3. GERENCIADORES
	    // Venda: precisa de 4 repositórios
	    IGerenciadorVenda gv = new GerenciadorVenda(rv, rc, rVend, rVeic);

	    // Oficina: precisa de 3 repositórios
	    IGerenciadorOficina go = new GerenciadorOficina(rs, rc, rVeic);

	    // Relatorio: precisa de 2 repositórios
	    IGerenciadorRelatorio gr = new GerenciadorRelatorio(rv, rs);

	    // TestDrive: precisa de 3 repositórios
	    IGerenciadorTestDrive gt = new GerenciadorTestDrive(rTd, rc, rVeic);

	    // 3. INICIALIZAÇÃO DA INTERFACE (criar interfaces gráficas (GUI), Utilizando JavaFX: (MENUPRINCIPAL e TELAS - (*alterações*)
	    MenuPrincipal menu = new MenuPrincipal(gv, go, gr, gt);
	    System.out.println("SISTEMA AUTO DRIVE INICIALIZADO!");
	    menu.exibirMenu();
	}
}
