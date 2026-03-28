package com.example.ai_designcritic;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;

public class PdfExportService {

    public static void export(File file, String content) throws Exception {

        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("AI Design Critic Report")
                .setFontSize(18).simulateBold());

        document.add(new Paragraph("\n"));
        document.add(new Paragraph(content).setFontSize(11));

        document.close();
    }
}
