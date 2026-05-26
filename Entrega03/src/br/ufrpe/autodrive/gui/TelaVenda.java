package br.ufrpe.autodrive.gui;

import java.util.Scanner;
import java.util.List;
import br.ufrpe.autodrive.negocio.IGerenciadorVenda;
import br.ufrpe.autodrive.negocio.beans.Notificacao;

public class TelaVenda {
    private IGerenciadorVenda control; 

    public TelaVenda(IGerenciadorVenda gVenda) {
        this.control = gVenda;
    }

    public void exibir() {
        Scanner leitor = new Scanner(System.in);
        String op = "-1"; // Usar String no menu evita o bug do buffer do Scanner

        while (!op.equals("0")) {
            System.out.println("\n--- TELA DE VENDAS ---");
            System.out.println("1. Realizar Nova Venda");
            System.out.println("2. Verificar Necessidade de Reparo (Alertas)");
            System.out.println("0. Voltar ao Menu");
            System.out.print("Escolha uma opção: ");
            
            op = leitor.nextLine(); // Lemos como linha inteira para não sobrar "Enter" no buffer

            if (op.equals("1")) this.botaoRealizarVenda(leitor);
            if (op.equals("2")) this.botaoVerificarAlertas();
        }
    }

    // Ajustado nome para bater com a chamada do menu e recebendo o leitor
    private void botaoRealizarVenda(Scanner leitor) {
        System.out.println("\n--- EFETUAR NOVA VENDA ---");

        try {
            System.out.print("Digite o número da venda: ");
            int numero = Integer.parseInt(leitor.nextLine());

            System.out.print("CPF do Cliente: ");
            String cpfCliente = leitor.nextLine();

            System.out.print("Chassi do Veículo: ");
            String chassi = leitor.nextLine();

            System.out.print("Nome do Vendedor: ");
            String nomeVendedor = leitor.nextLine();

            System.out.print("Valor de Entrada: R$ ");
            double entrada = Double.parseDouble(leitor.nextLine());

            boolean sucesso = control.efetuarVenda(numero, cpfCliente, chassi, nomeVendedor, entrada);

            if (sucesso) {
                System.out.println("\n✅ SUCESSO: Venda registrada!");
            } else {
                System.out.println("\n❌ ERRO: Dados inválidos ou veículo indisponível.");
            }

        } catch (NumberFormatException e) {
            System.out.println("\n⚠️ ERRO: Use apenas números em 'Número' e 'Entrada'.");
        }
    }
    
    private void botaoVerificarAlertas() {
        System.out.println("\n--- BUSCANDO ALERTAS DE REVISÃO ---");
        List<Notificacao> alertas = control.listarAlertasRevisao(); 
    
        if (alertas == null || alertas.isEmpty()) {
            System.out.println("Nenhum veículo precisa de revisão no momento.");
        } else {
            for (Notificacao n : alertas) {
                System.out.println("[ALERTA] Cliente: " + n.getCliente().getNome() + 
                                   " | Veículo: " + n.getVeiculo().getModelo() + 
                                   " | KM: " + n.getQuilometragem());
            }
        }
    }
}
