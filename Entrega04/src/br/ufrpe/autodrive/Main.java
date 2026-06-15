package br.ufrpe.autodrive;

import java.time.LocalDateTime;

import br.ufrpe.autodrive.dados.IRepositorioClientes;
import br.ufrpe.autodrive.dados.IRepositorioMecanicos;
import br.ufrpe.autodrive.dados.IRepositorioOS;
import br.ufrpe.autodrive.dados.IRepositorioPecas;
import br.ufrpe.autodrive.dados.IRepositorioTD;
import br.ufrpe.autodrive.dados.IRepositorioVeiculos;
import br.ufrpe.autodrive.dados.IRepositorioVendas;
import br.ufrpe.autodrive.dados.IRepositorioVendedores;
import br.ufrpe.autodrive.dados.RepositorioClientesArray;
import br.ufrpe.autodrive.dados.RepositorioMecanicosArray;
import br.ufrpe.autodrive.dados.RepositorioOsArray;
import br.ufrpe.autodrive.dados.RepositorioPecasArray;
import br.ufrpe.autodrive.dados.RepositorioTestDriveArray;
import br.ufrpe.autodrive.dados.RepositorioVeiculosArray;
import br.ufrpe.autodrive.dados.RepositorioVendasArray;
import br.ufrpe.autodrive.dados.RepositorioVendedoresArray;
import br.ufrpe.autodrive.gui.ScreenManager;
import br.ufrpe.autodrive.negocio.GerenciadorCadastro;
import br.ufrpe.autodrive.negocio.GerenciadorEstoquePecas;
import br.ufrpe.autodrive.negocio.GerenciadorOficina;
import br.ufrpe.autodrive.negocio.GerenciadorRelatorio;
import br.ufrpe.autodrive.negocio.GerenciadorTestDrive;
import br.ufrpe.autodrive.negocio.GerenciadorVenda;
import br.ufrpe.autodrive.negocio.IGerenciadorCadastro;
import br.ufrpe.autodrive.negocio.IGerenciadorEstoquePecas;
import br.ufrpe.autodrive.negocio.IGerenciadorOficina;
import br.ufrpe.autodrive.negocio.IGerenciadorRelatorio;
import br.ufrpe.autodrive.negocio.IGerenciadorTestDrive;
import br.ufrpe.autodrive.negocio.IGerenciadorVenda;
import br.ufrpe.autodrive.negocio.beans.Cliente;
import br.ufrpe.autodrive.negocio.beans.MaoDeObra;
import br.ufrpe.autodrive.negocio.beans.Mecanico;
import br.ufrpe.autodrive.negocio.beans.Notificacao;
import br.ufrpe.autodrive.negocio.beans.OrdemServico;
import br.ufrpe.autodrive.negocio.beans.Pecas;
import br.ufrpe.autodrive.negocio.beans.StatusOS;
import br.ufrpe.autodrive.negocio.beans.StatusVeiculo;
import br.ufrpe.autodrive.negocio.beans.Veiculo;
import br.ufrpe.autodrive.negocio.beans.VeiculoNovo;
import br.ufrpe.autodrive.negocio.beans.VeiculoSeminovo;
import br.ufrpe.autodrive.negocio.beans.Vendedor;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        
        IRepositorioVendas repoVendas = RepositorioVendasArray.getInstance();
        IRepositorioClientes repoClientes = RepositorioClientesArray.getInstance();
        IRepositorioVeiculos repoVeiculos = RepositorioVeiculosArray.getInstance();
        IRepositorioVendedores repoVendedores = RepositorioVendedoresArray.getInstance();
        IRepositorioOS repoOS = RepositorioOsArray.getInstance();
        IRepositorioTD repoTestDrive = RepositorioTestDriveArray.getInstance();
        IRepositorioMecanicos repoMecanicos = RepositorioMecanicosArray.getInstance();
        IRepositorioPecas repoPecas = RepositorioPecasArray.getInstance();
        
        if (repoMecanicos.procurarMecanico("Mario") == null) {
            repoMecanicos.adicionarMecanico(new Mecanico("Mario", true));
        }
        if (repoMecanicos.procurarMecanico("Luigi") == null) {
            repoMecanicos.adicionarMecanico(new Mecanico("Luigi", true));
        }

        if (repoPecas.buscarPorCodigo("EST-001") == null) repoPecas.salvar(new Pecas("Óleo de Motor Sintético 5W30", "EST-001", 45.00, 10));
        if (repoPecas.buscarPorCodigo("EST-002") == null) repoPecas.salvar(new Pecas("Filtro de Óleo", "EST-002", 25.00, 10));
        if (repoPecas.buscarPorCodigo("EST-003") == null) repoPecas.salvar(new Pecas("Filtro de Ar", "EST-003", 30.00, 10));
        if (repoPecas.buscarPorCodigo("EST-004") == null) repoPecas.salvar(new Pecas("Filtro de Combustível", "EST-004", 40.00, 10));
        if (repoPecas.buscarPorCodigo("EST-005") == null) repoPecas.salvar(new Pecas("Jogo de Velas de Ignição", "EST-005", 60.00, 10));
        if (repoPecas.buscarPorCodigo("EST-006") == null) repoPecas.salvar(new Pecas("Pastilha de Freio", "EST-006", 80.00, 10));
        if (repoPecas.buscarPorCodigo("EST-007") == null) repoPecas.salvar(new Pecas("Disco de Freio", "EST-007", 120.00, 10));
        if (repoPecas.buscarPorCodigo("EST-008") == null) repoPecas.salvar(new Pecas("Correia Dentada", "EST-008", 90.00, 10));
        if (repoPecas.buscarPorCodigo("EST-009") == null) repoPecas.salvar(new Pecas("Bateria 60Ah", "EST-009", 350.00, 10));
        if (repoPecas.buscarPorCodigo("EST-010") == null) repoPecas.salvar(new Pecas("Fluido de Radiador", "EST-010", 35.00, 10));
        
        IGerenciadorVenda gVenda = new GerenciadorVenda(repoVendas, repoClientes, repoVendedores, repoVeiculos);
        IGerenciadorOficina gOficina = new GerenciadorOficina(repoOS, repoClientes, repoVeiculos, repoMecanicos);
        IGerenciadorRelatorio gRelatorio = new GerenciadorRelatorio(repoVendas, repoOS);
        IGerenciadorTestDrive gTestDrive = new GerenciadorTestDrive(repoTestDrive, repoClientes, repoVeiculos);
        IGerenciadorCadastro gCadastro = new GerenciadorCadastro(repoClientes, repoVeiculos);
        IGerenciadorEstoquePecas gEstoque = new GerenciadorEstoquePecas(repoPecas);
        
        Cliente c1 = new Cliente("Samuel Silva", "123.456.789-00", "CNH12345", "samuel@email.com", "(81) 99999-9999");
        Cliente c2 = new Cliente("Maria Souza", "987.654.321-11", "CNH54321", "maria@email.com", "(81) 98888-8888");
        Cliente c3 = new Cliente("Yuri Neves", "321.122.567-12", "CNH1567", "yuri@gmail.com", "(81)9199-1919");
                
        if (repoClientes.procurarCliente("123.456.789-00") == null) {
            repoClientes.adicionarCliente(c1);
        }
        if (repoClientes.procurarCliente("987.654.321-11") == null) {
            repoClientes.adicionarCliente(c2);
        }
        if (repoClientes.procurarCliente("321.122.567-12") == null) {
            repoClientes.adicionarCliente(c3);
        }
        
        Vendedor vend1 = new Vendedor("Artur M.", 0.05);
        Vendedor vend2 = new Vendedor("Otavio R.", 0.05); 
        
        if (repoVendedores.procurarVendedor("Artur M.") == null) {
            repoVendedores.adicionarVendedor(vend1);
        }
        if (repoVendedores.procurarVendedor("Otavio R.") == null) {
            repoVendedores.adicionarVendedor(vend2);
        }
        
        VeiculoNovo v1 = new VeiculoNovo("93X82KAA", "RENAVAM111", "Chevrolet Onix", 2026, 75000.00);
        if (repoVeiculos.procurarVeiculo("93X82KAA") == null) {
            repoVeiculos.adicionarVeiculo(v1); 
        }
        
        VeiculoSeminovo v2 = new VeiculoSeminovo("82J91PBB", "RENAVAM222", "Ford Ka", 2021, 45000.00, 5000.0);
        if (repoVeiculos.procurarVeiculo("82J91PBB") == null) {
            repoVeiculos.adicionarVeiculo(v2);
        }

        VeiculoSeminovo vAlerta = new VeiculoSeminovo("CHASSIALERTA", "RENAVAM333", "Toyota Corolla", 2020, 90000.00, 15000.0);
        if (repoVeiculos.procurarVeiculo("CHASSIALERTA") == null) {
            repoVeiculos.adicionarVeiculo(vAlerta);
        }
        
        VeiculoSeminovo vSemAlerta = new VeiculoSeminovo("CHASSILIMPO", "RENAVAM444", "Fiat Uno", 2022, 30000.00, 2000.0);
        if (repoVeiculos.procurarVeiculo("CHASSILIMPO") == null) {
            repoVeiculos.adicionarVeiculo(vSemAlerta);
        }

        VeiculoNovo carRelatorio1 = new VeiculoNovo("CHASSIREP1", "RENREP1", "Hyundai HB20", 2025, 80000.00);
        VeiculoNovo carRelatorio2 = new VeiculoNovo("CHASSIREP2", "RENREP2", "Jeep Renegade", 2024, 110000.00);
        VeiculoNovo carRelatorio3 = new VeiculoNovo("CHASSIREP3", "RENREP3", "Fiat Pulse", 2025, 95000.00);
        
        if (repoVeiculos.procurarVeiculo("CHASSIREP1") == null) repoVeiculos.adicionarVeiculo(carRelatorio1);
        if (repoVeiculos.procurarVeiculo("CHASSIREP2") == null) repoVeiculos.adicionarVeiculo(carRelatorio2);
        if (repoVeiculos.procurarVeiculo("CHASSIREP3") == null) repoVeiculos.adicionarVeiculo(carRelatorio3);
        
        if (repoVendas.listarTodasVendas().stream().noneMatch(v -> v.getVeiculo().getChassi().equals("CHASSIREP1"))) {
            gVenda.efetuarVenda(0, "123.456.789-00", "CHASSIREP1", "Artur M.", 20000.00, LocalDateTime.of(2026, 4, 15, 14, 30));
        }
        if (repoVendas.listarTodasVendas().stream().noneMatch(v -> v.getVeiculo().getChassi().equals("CHASSIREP2"))) {
            gVenda.efetuarVenda(0, "987.654.321-11", "CHASSIREP2", "Otavio R.", 35000.00, LocalDateTime.of(2026, 5, 10, 10, 15));
        }
        if (repoVendas.listarTodasVendas().stream().noneMatch(v -> v.getVeiculo().getChassi().equals("CHASSIREP3"))) {
            gVenda.efetuarVenda(0, "321.122.567-12", "CHASSIREP3", "Artur M.", 15000.00, LocalDateTime.of(2026, 6, 2, 16, 45));
        }
        
        if (repoVeiculos.procurarVeiculo("CHASSI_FILA_1") == null) {
            repoVeiculos.adicionarVeiculo(new VeiculoSeminovo("CHASSI_FILA_1", "AAA1111", "Fiat Uno", 2015, 25000.0, 80000.0));
        }
        if (repoVeiculos.procurarVeiculo("CHASSI_FILA_2") == null) {
            repoVeiculos.adicionarVeiculo(new VeiculoSeminovo("CHASSI_FILA_2", "BBB2222", "Ford Ka", 2018, 35000.0, 50000.0));
        }
        if (repoVeiculos.procurarVeiculo("CHASSI_FILA_3") == null) {
            repoVeiculos.adicionarVeiculo(new VeiculoSeminovo("CHASSI_FILA_3", "CCC3333", "Chevrolet Onix", 2020, 55000.0, 30000.0));
        }

        if (repoOS.listarTodas().isEmpty()) {
            System.out.println("\n--- [TESTE YURI] Iniciando Simulação Automatizada de Fila ---");
            gOficina.abrirOS(c1.getCpf(), "CHASSI_FILA_1"); 
            gOficina.abrirOS(c1.getCpf(), "CHASSI_FILA_2"); 
            gOficina.abrirOS(c1.getCpf(), "CHASSI_FILA_3"); 
        }

        if (repoOS.listarTodas() != null && !repoOS.listarTodas().isEmpty()) {
            int numeroOS1 = repoOS.listarTodas().get(0).getNumero();
            gOficina.finalizarServico(numeroOS1);
        }

        if (repoVeiculos.procurarVeiculo("CHASSIALERTA2") == null) {
            repoVeiculos.adicionarVeiculo(new VeiculoSeminovo("CHASSIALERTA2", "RENAVAM555", "Jeep Compass", 2023, 120000.00, 8500.0));
        }

        if (repoVendas.listarTodasVendas().stream().noneMatch(v -> v.getVeiculo().getChassi().equals("CHASSIALERTA"))) {
            gVenda.efetuarVenda(0, c2.getCpf(), "CHASSIALERTA", "Artur M.", 90000.00, LocalDateTime.now().minusMonths(8)); 
        }
        if (repoVendas.listarTodasVendas().stream().noneMatch(v -> v.getVeiculo().getChassi().equals("CHASSIALERTA2"))) {
            gVenda.efetuarVenda(0, c3.getCpf(), "CHASSIALERTA2", "Artur M.", 120000.00, LocalDateTime.now().minusMonths(1));
        }
        if (repoVendas.listarTodasVendas().stream().noneMatch(v -> v.getVeiculo().getChassi().equals("CHASSILIMPO"))) {
            gVenda.efetuarVenda(0, c1.getCpf(), "CHASSILIMPO", "Otavio R.", 30000.00, LocalDateTime.now().minusMonths(2)); 
        }
        
        Mecanico mec1 = repoMecanicos.procurarMecanico("Mario");
        Mecanico mec2 = repoMecanicos.procurarMecanico("Luigi");

        if (mec1 != null) mec1.setDisponivel(true);
        if (mec2 != null) mec2.setDisponivel(true);

        VeiculoSeminovo vOficina1 = new VeiculoSeminovo("CHASSI_OS_1", "KGO1A23", "Chevrolet Onix", 2021, 62000.00, 45000.0);
        VeiculoSeminovo vOficina2 = new VeiculoSeminovo("CHASSI_OS_2", "MUT4B56", "Toyota Corolla", 2022, 115000.00, 32000.0);
        
        if (repoVeiculos.procurarVeiculo("CHASSI_OS_1") == null) {
            repoVeiculos.adicionarVeiculo(vOficina1);
        }
        if (repoVeiculos.procurarVeiculo("CHASSI_OS_2") == null) {
            repoVeiculos.adicionarVeiculo(vOficina2);
        }

        if (mec1 != null) mec1.setDisponivel(true);
        if (mec2 != null) mec2.setDisponivel(true);

        if (repoOS.listarTodas().stream().noneMatch(os -> os.getVeiculo() != null && os.getVeiculo().getChassi().equals("CHASSI_OS_1"))) {
            OrdemServico os1 = new OrdemServico();
            os1.setCliente(c1);
            os1.setVeiculo(vOficina1);
            os1.setMecanico(mec1);
            
            os1.setDataAbertura("10/06/2026 08:00");
            os1.setDataFechamento("10/06/2026 14:30");
            
            os1.getListaPecas().add(new Pecas("Kit Filtros e Óleo Sintético", "PC-001", 250.00, 1));
            os1.getListaPecas().add(new Pecas("Jogo de Velas Iridium", "PC-002", 180.00, 1));
            
            // 🟢 ATUALIZAÇÃO DO CONSTRUTOR AQUI
            os1.getListaServicos().add(new MaoDeObra("Mão de Obra Revisão Periódica", 200.00, mec1));
            
            os1.setStatus(StatusOS.FINALIZADA);
            os1.setValorTotal(250.00 + 180.00 + 200.00);
            
            repoOS.salvar(os1);
        }

        if (repoOS.listarTodas().stream().noneMatch(os -> os.getVeiculo() != null && os.getVeiculo().getChassi().equals("CHASSI_OS_2"))) {
            OrdemServico os2 = new OrdemServico();
            os2.setCliente(c2);
            os2.setVeiculo(vOficina2);
            os2.setMecanico(mec2);
            
            os2.setDataAbertura("11/06/2026 09:00");
            os2.setDataFechamento("11/06/2026 17:00");
            
            os2.getListaPecas().add(new Pecas("Par de Discos de Freio Ventilados", "PC-003", 450.00, 1));
            os2.getListaPecas().add(new Pecas("Jogo de Pastilhas de Freio Cerâmica", "PC-004", 220.00, 1));
            
            // 🟢 ATUALIZAÇÃO DO CONSTRUTOR AQUI
            os2.getListaServicos().add(new MaoDeObra("Alinhamento, Balanceamento e Freios", 150.00, mec2));
            
            os2.setStatus(StatusOS.FINALIZADA);
            os2.setValorTotal(450.00 + 220.00 + 150.00);
            
            repoOS.salvar(os2);
        }

        if (mec1 != null) mec1.setDisponivel(false); 
        if (mec2 != null) mec2.setDisponivel(false); 

        VeiculoNovo vVenda1 = new VeiculoNovo("CHASSI_V_1", "AAA0A01", "Jeep Compass", 2026, 185000.00);
        VeiculoSeminovo vVenda2 = new VeiculoSeminovo("CHASSI_V_2", "BBB0B02", "Honda Civic", 2023, 140000.00, 28000.0);
        VeiculoNovo vVenda3 = new VeiculoNovo("CHASSI_V_3", "CCC0C03", "Fiat Toro", 2026, 150000.00);
        VeiculoSeminovo vVenda4 = new VeiculoSeminovo("CHASSI_V_4", "DDD0D04", "Volkswagen Golf", 2020, 110000.00, 65000.0);
        VeiculoNovo vVenda5 = new VeiculoNovo("CHASSI_V_5", "EEE0E05", "Hyundai Creta", 2026, 135000.00);

        vVenda1.setStatus(StatusVeiculo.DISPONIVEL);
        vVenda2.setStatus(StatusVeiculo.DISPONIVEL);
        vVenda3.setStatus(StatusVeiculo.DISPONIVEL);
        vVenda4.setStatus(StatusVeiculo.DISPONIVEL);
        vVenda5.setStatus(StatusVeiculo.DISPONIVEL);

        VeiculoSeminovo vOficina3 = new VeiculoSeminovo("CHASSI_M_3", "FFF0F06", "Ford Ka", 2019, 48000.00, 82000.0);
        VeiculoSeminovo vOficina4 = new VeiculoSeminovo("CHASSI_M_4", "GGG0G07", "Renault Sandero", 2020, 52000.00, 71000.0);
        VeiculoSeminovo vOficina5 = new VeiculoSeminovo("CHASSI_M_5", "HHH0H08", "Toyota Hilux", 2021, 210000.00, 95000.0);
        VeiculoSeminovo vOficina6 = new VeiculoSeminovo("CHASSI_M_6", "III0I09", "Hyundai HB20", 2022, 68000.00, 40000.0);
        VeiculoSeminovo vOficina7 = new VeiculoSeminovo("CHASSI_M_7", "JJJ0J10", "Fiat Uno", 2018, 35000.00, 110000.0);

        vOficina3.setStatus(StatusVeiculo.DISPONIVEL);
        vOficina4.setStatus(StatusVeiculo.DISPONIVEL);
        vOficina5.setStatus(StatusVeiculo.DISPONIVEL);
        vOficina6.setStatus(StatusVeiculo.DISPONIVEL);
        vOficina7.setStatus(StatusVeiculo.DISPONIVEL);

        VeiculoNovo vTD1 = new VeiculoNovo("CHASSI_TD_1", "KKK0K11", "BYD Dolphin", 2026, 150000.00);
        VeiculoNovo vTD2 = new VeiculoNovo("CHASSI_TD_2", "LLL0L12", "GWM Ora 3", 2026, 160000.00);
        VeiculoSeminovo vTD3 = new VeiculoSeminovo("CHASSI_TD_3", "MMM0M13", "Nissan Kicks", 2024, 105000.00, 15000.0);

        vTD1.setStatus(StatusVeiculo.DISPONIVEL);
        vTD2.setStatus(StatusVeiculo.DISPONIVEL);
        vTD3.setStatus(StatusVeiculo.DISPONIVEL);

        Veiculo[] novosVeiculos = {
            vVenda1, vVenda2, vVenda3, vVenda4, vVenda5,
            vOficina3, vOficina4, vOficina5, vOficina6, vOficina7,
            vTD1, vTD2, vTD3
        };

        for (Veiculo v : novosVeiculos) {
            if (repoVeiculos.procurarVeiculo(v.getChassi()) == null) {
                repoVeiculos.adicionarVeiculo(v);
            }
        }
        
        VeiculoNovo vOficinaDisponivel = new VeiculoNovo("CHASSIOFICINA", "RENOF001", "Volkswagen Polo", 2026, 89000.00);
        if (repoVeiculos.procurarVeiculo("CHASSIOFICINA") == null) {
            repoVeiculos.adicionarVeiculo(vOficinaDisponivel);
        }
        
        ScreenManager.getInstance().setMainStage(primaryStage);
        ScreenManager.getInstance().injetarGerenciadores(gVenda, gOficina, gRelatorio, gTestDrive, gCadastro, gEstoque); 
        ScreenManager.getInstance().showMenuPrincipal();
    }
    
    public static void main(String[] args) {
        launch(args); 
    }
}
