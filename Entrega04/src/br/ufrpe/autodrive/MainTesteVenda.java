package br.ufrpe.autodrive;

import br.ufrpe.autodrive.dados.*;
import br.ufrpe.autodrive.negocio.*;
import br.ufrpe.autodrive.negocio.beans.*;
import java.time.LocalDateTime;

public class MainTesteVenda {
    public static void main(String[] args) {
        System.out.println("=== HOMOLOGAÇÃO ISOLADA: MÓDULO VENDAS ===");

        // 1. Inicializando Repositórios reais (Usando Singletons)
        IRepositorioVendas repoVendas = RepositorioVendasArray.getInstance();
        IRepositorioClientes repoClientes = RepositorioClientesArray.getInstance();
        IRepositorioVendedores repoVendedores = RepositorioVendedoresArray.getInstance();
        IRepositorioVeiculos repoVeiculos = RepositorioVeiculosArray.getInstance();

        GerenciadorVenda gerenciador = new GerenciadorVenda(repoVendas, repoClientes, repoVendedores, repoVeiculos);

        // 2. Criando massa de dados limpa com prefixo "V_"
        String cpfCliente = "55566677788";
        Cliente cliente = new Cliente("Comprador Teste", cpfCliente, "88888");
        repoClientes.adicionarCliente(cliente);

        // 🟢 CORREÇÃO DO VENDEDOR: Usando o construtor exato da sua classe Vendedor(nome, percentualComissao)
        String nomeVendedor = "Vendedor Estrela";
        Vendedor vendedor = new Vendedor(nomeVendedor, 0.02); // 2% de comissão
        repoVendedores.adicionarVendedor(vendedor);

        // Carro para venda de Sucesso (Preço base: R$ 100.000,00)
        Veiculo veiculoValido = new VeiculoNovo("CHASSI_V_OK", "VND-0001", "Corolla Venda", 2024, 100000.0);
        veiculoValido.setStatus(StatusVeiculo.DISPONIVEL);
        repoVeiculos.adicionarVeiculo(veiculoValido);

        // Carro travado na manutenção
        Veiculo veiculoOficina = new VeiculoNovo("CHASSI_V_MANUT", "VND-0002", "Marea Oficina", 2021, 30000.0);
        veiculoOficina.setStatus(StatusVeiculo.EM_MANUTENCAO);
        repoVeiculos.adicionarVeiculo(veiculoOficina);

     // ----------------------------------------------------------------------
        System.out.println("\n--- PASSO 1: Efetuando uma Venda Válida (Entrada R$ 20.000) ---");
        LocalDateTime dataVenda = LocalDateTime.now();
        int numeroVenda1 = 12345; // Esse número será gerado aleatoriamente pela sua classe Venda
        
        boolean vendaEfetuada = gerenciador.efetuarVenda(numeroVenda1, cpfCliente, "CHASSI_V_OK", nomeVendedor, 20000.0, dataVenda);
        
        System.out.println("Venda efetuada com sucesso? " + vendaEfetuada + " (Esperado: true)");
        System.out.println("Status final do carro vendido: " + veiculoValido.getStatus() + " (Esperado: VENDIDO)");
        
        // 🟢 AJUSTE SEGURO: Como o número é gerado por UUID.hashCode(), buscamos na lista completa pelo chassi
        Venda vendaGravada = null;
        for (Venda v : gerenciador.listarTodasVendas()) {
            if (v.getVeiculo().getChassi().equals("CHASSI_V_OK")) {
                vendaGravada = v;
                break;
            }
        }

        if (vendaGravada != null) {
            System.out.println("Número Real Gerado para a Venda: " + vendaGravada.getNumero());
            System.out.println("Valor Total Calculado (Preço + 10% Imposto): R$ " + vendaGravada.getValorTotal() + " (Esperado: R$ 110000.0)");
            System.out.println("Comissão acumulada pelo vendedor: R$ " + vendedor.getComissao() + " (Esperado: R$ 2000.0)");
        } else {
            System.out.println("ERRO: Venda não foi localizada no repositório persistente.");
        }

        // ----------------------------------------------------------------------
        System.out.println("\n--- PASSO 2: Testando Bloqueio de Entrada Abaixo do Mínimo (Entrada R$ 1.000) ---");
        // O REQ15 exige entrada mínima de R$ 5.000,00. Vamos forçar um erro enviando R$ 1.000,00
        Veiculo veiculoValido2 = new VeiculoNovo("CHASSI_V_BARRA", "VND-0003", "Civic Entrada", 2024, 120000.0);
        veiculoValido2.setStatus(StatusVeiculo.DISPONIVEL);
        repoVeiculos.adicionarVeiculo(veiculoValido2);

        int numeroVenda2 = 67890;
        boolean vendaRecusadaEntrada = gerenciador.efetuarVenda(numeroVenda2, cpfCliente, "CHASSI_V_BARRA", nomeVendedor, 1000.0, dataVenda);
        System.out.println("Venda aceita com entrada baixa? " + vendaRecusadaEntrada + " (Esperado: false)");

        // ----------------------------------------------------------------------
        System.out.println("\n--- PASSO 3: Testando Bloqueio de Carro em Manutenção ---");
        int numeroVenda3 = 11111;
        boolean vendaRecusadaManutencao = gerenciador.efetuarVenda(numeroVenda3, cpfCliente, "CHASSI_V_MANUT", nomeVendedor, 10000.0, dataVenda);
        System.out.println("Venda aceita para carro em manutenção? " + vendaRecusadaManutencao + " (Esperado: false)");
    }
}