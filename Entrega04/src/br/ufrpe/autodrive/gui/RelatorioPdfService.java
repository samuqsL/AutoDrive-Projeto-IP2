package br.ufrpe.autodrive.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import br.ufrpe.autodrive.negocio.beans.OrdemServico;
import br.ufrpe.autodrive.negocio.beans.Venda;

public class RelatorioPdfService {

    private static final Font FONTE_TITULO = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
    private static final Font FONTE_CORPO = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Centraliza o salvamento físico e abertura imediata do arquivo PDF
     */
    private static void salvarEVisualizarPdf(String nomeArquivo, PDFConteudoMontador montador) {
        File pastaRelatorios = new File("RelatorioPdf");
        if(!pastaRelatorios.exists()){
            pastaRelatorios.mkdirs();
        }

        String caminho = "RelatorioPdf" + File.separator + nomeArquivo + ".pdf";
        Document documento = new Document();

        try {
            PdfWriter.getInstance(documento, new FileOutputStream(caminho));
            documento.open();

            // Executa a montagem customizada passada via expressão lambda
            montador.montar(documento);

            documento.close();

            // Abre o leitor de PDF padrão do sistema operacional automaticamente
            File arquivoPdf = new File(caminho);
            if (arquivoPdf.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(arquivoPdf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- MÉTODOS PÚBLICOS CHAMADOS PELOS BOTÕES ---

    public static void exportarVendas(List<Venda> lista, String subtitulo) {
        String nomeLimpo = subtitulo.replaceAll("[^a-zA-Z0-9]", "_");
        salvarEVisualizarPdf("Relatorio_Vendas_" + nomeLimpo, doc -> {
            doc.add(new Paragraph("--- RELATÓRIO DE VENDAS (" + subtitulo + ") ---", FONTE_TITULO));
            doc.add(new Paragraph("\n"));

            if (lista.isEmpty()) {
                doc.add(new Paragraph("Nenhum registro encontrado.", FONTE_CORPO));
            } else {
                for (Venda v : lista) {
                    String dataFormatada = (v.getDataVenda() != null) ? v.getDataVenda().format(fmt) : "Sem Data";
                    String linha = "Nº: " + v.getNumero() +
                            " | Data: " + dataFormatada +
                            " | Vendedor: " + v.getVendedor().getNome() +
                            " | Cliente: " + v.getCliente().getNome() +
                            " | Veiculo: " + v.getVeiculo().getModelo() +
                            " | Entrada: " + v.getEntrada() +
                            " | Total: R$ " + v.getValorTotal();
                    doc.add(new Paragraph(linha, FONTE_CORPO));
                }
            }
        });
    }

    public static void exportarOficina(List<OrdemServico> lista) {
        salvarEVisualizarPdf("Relatorio_Oficina", doc -> {
            doc.add(new Paragraph("--- RELATÓRIO DE OFICINA ---", FONTE_TITULO));
            doc.add(new Paragraph("\n"));

            if (lista.isEmpty()) {
                doc.add(new Paragraph("Nenhuma OS encontrada.", FONTE_CORPO));
            } else {

                java.time.format.DateTimeFormatter formatador = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                for (OrdemServico os : lista) {
                    String dataFormatada = "N/A";

                    if(os.getDataAbertura() != null){
                        String testeData = os.getDataAbertura().toString();
                        if(testeData.contains("Value(") || testeData.contains("ParseCase")){
                            dataFormatada = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                        }
                        else{
                            dataFormatada = testeData;
                        }
                    }
                    String linha = "OS Nº: " + os.getNumero() +
                            " | Data: " + dataFormatada +
                            " | Cliente: " + os.getCliente().getNome() +
                            " | Status: " + os.getStatus();
                    doc.add(new Paragraph(linha, FONTE_CORPO));
                }
            }
        });
    }

    public static void exportarLucratividade(double[] lucros) {
        salvarEVisualizarPdf("Relatorio_Lucratividade", doc -> {
            doc.add(new Paragraph("--- RESUMO FINANCEIRO - LUCRATIVIDADE ---", FONTE_TITULO));
            doc.add(new Paragraph("\n"));

            double total = lucros[0] + lucros[1];
            doc.add(new Paragraph(String.format("Receita de Peças: R$ %.2f", lucros[0]), FONTE_CORPO));
            doc.add(new Paragraph(String.format("Receita de Serviços: R$ %.2f", lucros[1]), FONTE_CORPO));
            doc.add(new Paragraph("----------------------------------------", FONTE_CORPO));
            doc.add(new Paragraph(String.format("TOTAL ACUMULADO: R$ %.2f", total), FONTE_TITULO));
        });
    }

    @FunctionalInterface
    private interface PDFConteudoMontador {
        void montar(Document doc) throws Exception;
    }
}