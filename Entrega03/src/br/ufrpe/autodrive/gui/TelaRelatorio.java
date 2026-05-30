package br.ufrpe.autodrive.gui;

import br.ufrpe.autodrive.negocio.IGerenciadorRelatorio;
import br.ufrpe.autodrive.negocio.Relatorio;
import br.ufrpe.autodrive.negocio.beans.Venda;
import br.ufrpe.autodrive.negocio.beans.OrdemServico;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog; 
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class TelaRelatorio {

    private IGerenciadorRelatorio control;
    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    
    @FXML private TextArea txtAreaConsole;

    public void injetarGerenciador(IGerenciadorRelatorio gR) {
        this.control = gR;
    }

    @FXML
    public void carregarVendasGerais() {
        if (control != null) {
            Relatorio dados = control.gerarDadosRelatorio();
            exibirVendasNoTexto(dados.getListaVendas(), "GERAL");
        }
    }

    @FXML
    public void carregarVendasPorVendedor() {
        if (control == null) return;

        // 1. Criar e configurar o Pop-up para o nome do vendedor
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Filtro por Vendedor");
        dialog.setHeaderText("Relatório de Vendas por Vendedor");
        dialog.setContentText("Digite o nome completo do vendedor:");

        // 2. Mostrar o pop-up e esperar o usuário digitar
        Optional<String> resultado = dialog.showAndWait();

        // 3. Se o usuário digitou algo e clicou em OK
        if (resultado.isPresent() && !resultado.get().trim().isEmpty()) {
            String nome = resultado.get().trim();
            Relatorio dados = control.gerarDadosRelatorio();
            exibirVendasNoTexto(dados.filtrarPorVendedor(nome), "VENDEDOR: " + nome);
        }
    }

    @FXML
    public void carregarVendasPorPeriodo() {
        if (control == null) return;

        // Pop-up 1: Data de Início
        TextInputDialog dialogIni = new TextInputDialog("dd/mm/aaaa");
        dialogIni.setTitle("Filtro por Período");
        dialogIni.setHeaderText("Data de Início");
        dialogIni.setContentText("Digite a data inicial (dd/mm/aaaa):");
        Optional<String> resIni = dialogIni.showAndWait();

        if (resIni.isPresent()) {
            // Pop-up 2: Data de Fim
            TextInputDialog dialogFim = new TextInputDialog("dd/mm/aaaa");
            dialogFim.setTitle("Filtro por Período");
            dialogFim.setHeaderText("Data de Fim");
            dialogFim.setContentText("Digite a data final (dd/mm/aaaa):");
            Optional<String> resFim = dialogFim.showAndWait();

            if (resFim.isPresent()) {
                try {
                    LocalDate ini = LocalDate.parse(resIni.get().trim(), fmt);
                    LocalDate fim = LocalDate.parse(resFim.get().trim(), fmt);
                    Relatorio dados = control.gerarDadosRelatorio();
                    exibirVendasNoTexto(dados.filtrarPorPeriodo(ini, fim), "PERÍODO");
                } catch (Exception e) {
                    txtAreaConsole.setText("❌ Erro: Formato de data inválido! Use o padrão dd/MM/yyyy (Ex: 15/05/2026)");
                }
            }
        }
    }

    @FXML
    public void carregarGeralOficina() {
        if (control != null) {
            Relatorio dados = control.gerarDadosRelatorio();
            StringBuilder sb = new StringBuilder();
            sb.append("--- RELATÓRIO DE OFICINA ---\n");
            if (dados.getListaOs().isEmpty()) sb.append("Nenhuma OS encontrada.\n");

            for (OrdemServico os : dados.getListaOs()) {
                sb.append("OS Nº: ").append(os.getNumero())
                        .append(" | Cliente: ").append(os.getCliente().getNome())
                        .append(" | Status: ").append(os.getStatus()).append("\n");
            }
            txtAreaConsole.setText(sb.toString());
        }
    }

    @FXML
    public void carregarLucratividade() {
        if (control != null) {
            double[] lucros = control.gerarDadosRelatorio().calcularLucratividade();
            String resumo = String.format(
                    "--- RESUMO FINANCEIRO ---\n" +
                            "Receita de Peças: R$ %.2f\n" +
                            "Receita de Serviços: R$ %.2f\n" +
                            "TOTAL ACUMULADO: R$ %.2f\n",
                    lucros[0], lucros[1], (lucros[0] + lucros[1])
            );
            txtAreaConsole.setText(resumo);
        }
    }

    @FXML
    public void voltarParaMenu() {
        ScreenManager.getInstance().showMenuPrincipal();
    }

    private void exibirVendasNoTexto(List<Venda> lista, String titulo) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- RELATÓRIO DE VENDAS (").append(titulo).append(") ---\n");

        if (lista.isEmpty()) {
            sb.append("Nenhum registro encontrado.\n");
        } else {
            for (Venda v : lista) {
                sb.append("Nº: ").append(v.getNumero())
                        .append(" | Vendedor: ").append(v.getVendedor().getNome())
                        .append(" | Total: R$ ").append(v.getValorTotal()).append("\n");
            }
        }
        txtAreaConsole.setText(sb.toString());
    }
}
