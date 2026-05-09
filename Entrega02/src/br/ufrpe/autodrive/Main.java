package model;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("========== SISTEMA AUTODRIVE - TESTE INTEGRADO ==========");

            // 1. CADASTRO DE ATORES
            Cliente cliente1 = new Cliente("Ricardo Lima", "123.456.789-00", "CNH-555", "ricardo@email.com", "81-98888-7777");
            Vendedor vendedor1 = new Vendedor("Ana Paula", 0.03); 
            Mecanico mecanico1 = new Mecanico("Mestre Yoda", 120.0, true); 

            // 2. CADASTRO DE VEÍCULOS
            VeiculoNovo civic = new VeiculoNovo("PXZ-2026", "VIN-001", "Honda Civic G11", 2026, 180000.0);
            VeiculoSeminovo renegade = new VeiculoSeminovo("OFF-4X4", "VIN-999", "Jeep Renegade", 2022, 110000.0, 35000.0);

            // 3. TESTE DE OFICINA
            System.out.println("\n>>> TESTE: OFICINA");
            OrdemServico os1 = new OrdemServico(2001, "20/04/2026", cliente1, renegade);
            
            MaoDeObra servico = new MaoDeObra("Revisão Geral", 100.0, 2.0, mecanico1);
            Pecas peca = new Pecas("Filtro de Ar", "F01", 50.0, 10);
            
            os1.adicionarServico(servico);
            os1.adicionarPeca(peca, 1);

            os1.marcarComoPago();
            os1.finalizarOS();

            // 4. TESTE DE VENDA
            System.out.println("\n>>> TESTE: VENDA");
            Venda venda1 = new Venda(cliente1, vendedor1, civic, 175000.0);
            venda1.setEntrada(60000.0);
            venda1.realizarVenda();

            // 5. TESTE DE NOTIFICAÇÃO
            System.out.println("\n>>> TESTE: NOTIFICAÇÃO");
            Notificacao nota = new Notificacao(10500.0, 0, "20/04/2026", 5, cliente1, civic);
            nota.notificarCliente();

            // 6. TESTE DE RELATÓRIO (Ajustado para a sua classe Relatorio)
            System.out.println("\n>>> TESTE: PROCESSANDO RELATÓRIOS");
            Relatorio rel = new Relatorio();
            
            // Alimentando o relatório com os dados criados acima
            rel.adicionarVenda(venda1);
            rel.adicionarOS(os1);
            
            // Chamando os seus métodos exatamente como estão na classe
            rel.relatorioVendas();
            rel.emitirRelatorioOS();
            rel.relatorioLucratividadeOficina();

            System.out.println("\n================ TESTE CONCLUÍDO ================");

        } catch (Exception e) {
            System.err.println("\n*** ERRO NO TESTE: " + e.getMessage());
            e.printStackTrace(); // Ajuda a ver onde faltou um getter se der erro
        }
    }
}
