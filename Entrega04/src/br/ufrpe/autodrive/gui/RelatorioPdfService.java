package br.ufrpe.autodrive.gui;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.Phrase;


import br.ufrpe.autodrive.negocio.beans.OrdemServico;
import br.ufrpe.autodrive.negocio.beans.Venda;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RelatorioPdfService {

    private static final BaseColor COR_PRIMARIA = new BaseColor(41, 128, 185);
    private static final BaseColor COR_TEXTO = new BaseColor(44, 62, 80);
    private static final BaseColor COR_LINHA = new BaseColor(220, 224, 230);

    private static final Font FONTE_TITULO = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, COR_PRIMARIA);
    private static final Font FONTE_CORPO = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, COR_TEXTO);
    private static final Font FONTE_DASH = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, COR_PRIMARIA);
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static void adicionarLinhaComoTabela(Document doc, String linha) throws Exception {
        // Se for linha decorativa , ignora para não quebrar o layout
        if (linha.contains("---")) return;

        // Divide a string nos pontos onde você colocou " | " ou "  |  "
        String[] partes = linha.split("\\s*\\|\\s*");

        int totalColunas = partes.length;

        PdfPTable tabela = new PdfPTable(totalColunas);
        tabela.setWidthPercentage(100);
        tabela.setSpacingAfter(5f);

        float[] larguras = new float[totalColunas];
        for (int i = 0; i < totalColunas; i++) {
            String texto = partes[i].toLowerCase();

            // Nº, OS Nº e Data ganham o maior espaço agora
            if (texto.contains("nº") || texto.contains("data")) {
                larguras[i] = 2.2f;
            }
            // Valores financeiros e totais mantêm um tamanho confortável
            else if (texto.contains("total") || texto.contains("entrada") || texto.contains("valor")) {
                larguras[i] = 1.6f;
            }
            // Vendedor, Cliente, Veículo, Chassi e Status agora ficam mais compactos
            else {
                larguras[i] = 1.2f;
            }
        }
        tabela.setWidths(larguras);

        for (String parte : partes) {
            String[] chaveValor = parte.split(":\\s*", 2);
            Paragraph p = new Paragraph();

            // Fonte tamanho 8.5 garante que o texto caiba mesmo nas colunas mais apertadas
            Font fonteChave = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, new BaseColor(100, 110, 120));
            Font fonteValor = new Font(Font.FontFamily.HELVETICA, 8.5f, Font.NORMAL, new BaseColor(44, 62, 80));

            if (chaveValor.length == 2) {
                p.add(new Phrase(chaveValor[0] + ": ", fonteChave));
                p.add(new Phrase(chaveValor[1], fonteValor));
            } else {
                p.add(new Phrase(parte, fonteValor));
            }

            PdfPCell celula = new PdfPCell(p);
            celula.setPadding(4); // Padding reduzido para otimizar o espaço interno
            celula.setBackgroundColor(new BaseColor(248, 249, 250));
            celula.setBorderColor(new BaseColor(225, 228, 232));
            celula.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tabela.addCell(celula);
        }
        doc.add(tabela);

    }


    /**
     * Centraliza o salvamento físico e abertura imediata do arquivo PDF
     */
    private static void salvarEVisualizarPdf(String nomeArquivo, PDFConteudoMontador montador) {
        File pastaRelatorios = new File("RelatorioPdf");
        if(!pastaRelatorios.exists()){
            pastaRelatorios.mkdirs();
        }

        String caminho = "RelatorioPdf" + File.separator + nomeArquivo + ".pdf";
        Document documento = new Document(com.itextpdf.text.PageSize.A4, 36, 36, 40, 40);

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

            Paragraph pTitulo = new Paragraph("RELATÓRIO DE VENDAS\n" + subtitulo.toUpperCase(), FONTE_TITULO);
            pTitulo.setAlignment(Element.ALIGN_CENTER);
            pTitulo.setSpacingAfter(20);
            doc.add(pTitulo);

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
                            " | Chassi: " + v.getVeiculo().getChassi() +
                            " | Entrada: R$ " + v.getEntrada() +
                            " | Total: R$ " + v.getValorTotal();

                    adicionarLinhaComoTabela(doc, linha);
                }
            }
        });
    }

    public static void exportarOficina(List<OrdemServico> lista) {
        salvarEVisualizarPdf("Relatorio_Oficina", doc -> {
            Paragraph pTitulo = new Paragraph("RELATÓRIO DE OFICINA", FONTE_TITULO);
            pTitulo.setAlignment(Element.ALIGN_CENTER);
            pTitulo.setSpacingAfter(20);
            doc.add(pTitulo);

            if (lista.isEmpty()) {
                Paragraph pVazio = new Paragraph("Nenhuma OS encontrada.", FONTE_CORPO);
                pVazio.setSpacingAfter(10);
                doc.add(pVazio);
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

                    adicionarLinhaComoTabela(doc, linha);
                }
            }
        });
    }

    public static void exportarLucratividade(double[] lucros) {
        salvarEVisualizarPdf("Relatorio_Lucratividade", doc -> {
            Paragraph pTitulo = new Paragraph("RESUMO FINANCEIRO - LUCRATIVIDADE", FONTE_TITULO);
            pTitulo.setSpacingAfter(20);
            doc.add(pTitulo);

            double total = lucros[0] + lucros[1];

            adicionarLinhaComoTabela(doc, String.format("Tipo: Receita de Peças | Valor: R$ %.2f", lucros[0]));
            adicionarLinhaComoTabela(doc, String.format("Tipo: Receita de Serviços | Valor: R$ %.2f", lucros[1]));

            doc.add(new Paragraph("\n"));
            doc.add(new Paragraph(String.format("TOTAL ACUMULADO: R$ %.2f", total), FONTE_TITULO));
        });
    }

    @FunctionalInterface
    private interface PDFConteudoMontador {
        void montar(Document doc) throws Exception;
    }
}
