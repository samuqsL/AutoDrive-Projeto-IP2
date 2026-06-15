package br.ufrpe.autodrive.gui;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import br.ufrpe.autodrive.negocio.beans.Notificacao;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class AlertasPdfService {

    private static final Font FONTE_TITULO = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
    private static final Font FONTE_SUBTITULO = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font FONTE_CORPO = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

    /**
     * Interface funcional para espelhar a estrutura inteligente usada no RelatorioPdfService
     */
    @FunctionalInterface
    private interface PDFConteudoMontador {
        void montar(Document doc) throws Exception;
    }

    private static void salvarEVisualizarPdf(String nomeArquivo, PDFConteudoMontador montador) {
        // Cria a pasta "alertas" no mesmo nível de "src/", "dados/"
        File pastaAlertas = new File("alertas");
        if (!pastaAlertas.exists()) {
            pastaAlertas.mkdirs();
        }

        String caminho = "alertas" + File.separator + nomeArquivo + ".pdf";
        Document documento = new Document();

        try {
            PdfWriter.getInstance(documento, new FileOutputStream(caminho));
            documento.open();

            // Executa a montagem customizada passada por expressão lambda
            montador.montar(documento);

            documento.close();

            // Abre o leitor de PDF padrão do sistema operacional automaticamente
            File arquivoPdf = new File(caminho);
            if (arquivoPdf.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(arquivoPdf);
            }

        } catch (Exception e) {
            System.err.println("-> [AlertasPdfService] Erro ao gerar PDF de Alertas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método principal que será chamado pelo seu botão
     */
    public static void exportarAlertasRevisao(List<Notificacao> alertas) {
        salvarEVisualizarPdf("Alertas_Revisao_Ativos", doc -> {
            doc.add(new Paragraph("=========================================", FONTE_SUBTITULO));
            doc.add(new Paragraph(" ⚠️ ALERTAS DE REVISÃO ATIVOS ", FONTE_TITULO));
            doc.add(new Paragraph("=========================================", FONTE_SUBTITULO));
            doc.add(new Paragraph("\n"));

            if (alertas == null || alertas.isEmpty()) {
                doc.add(new Paragraph("Nenhum veículo precisa de revisão no momento.", FONTE_CORPO));
            } else {
                for (Notificacao n : alertas) {
                    doc.add(new Paragraph("👤 CLIENTE: " + n.getCliente().getNome(), FONTE_CORPO));
                    doc.add(new Paragraph("   └─ CPF: " + n.getCliente().getCpf(), FONTE_CORPO));
                    
                    if (n.getCliente().getEmail() != null && !n.getCliente().getEmail().isBlank()) {
                        doc.add(new Paragraph("   └─ E-mail: " + n.getCliente().getEmail(), FONTE_CORPO));
                    }
                    
                    doc.add(new Paragraph("🚗 VEÍCULO: " + n.getVeiculo().getModelo(), FONTE_CORPO));
                    doc.add(new Paragraph("   └─ Chassi: " + n.getVeiculo().getChassi(), FONTE_CORPO));
                    doc.add(new Paragraph("   └─ Km Atual: " + n.getQuilometragem() + " km", FONTE_CORPO));
                    doc.add(new Paragraph("----------------------------------------------------------------------------------", FONTE_CORPO));
                    doc.add(new Paragraph("\n"));
                }
            }
        });
    }
}