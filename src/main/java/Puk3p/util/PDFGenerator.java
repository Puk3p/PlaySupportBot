package Puk3p.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class PDFGenerator {

    private static final String OUTPUT_DIR = "tickets";
    private static final String FONT_PATH = "Aptos.ttf";
    private static final int MAX_MESSAGES = 500;

    public static String generateTicketPDF(String user, String reason, String staff, String timestamp, TextChannel channel, String categoryPath) {
        String timeStampTicket = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm"));
        String fileName = user + "-ticket_" + timeStampTicket + ".pdf";
        String filePath = categoryPath  + File.separator + fileName;

        File directory = new File(categoryPath);
        if (!directory.exists() && !directory.mkdirs()) {
            System.err.println("Nu s-a putut crea directorul pentru salvarea PDF-urilor: " + categoryPath);
            return null;
        }

        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            Font font = initializeFont();
            if (font == null) {
                throw new IOException("Nu s-a putut încărca fontul: " + FONT_PATH);
            }

            writeTicketDetails(document, font, user, reason, staff, timestamp);
            writeChannelMessages(document, font, channel);

            System.out.println("[PDFGenerator] PDF generat cu succes: " + new File(filePath).getAbsolutePath());
        } catch (Exception e) {
            System.err.println("[PDFGenerator] Eroare la generarea PDF-ului: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            document.close();
        }

        return filePath;
    }

    private static Font initializeFont() {
        try {
            BaseFont baseFont = BaseFont.createFont(
                    PDFGenerator.class.getClassLoader().getResource(FONT_PATH).toString(),
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
            );
            return new Font(baseFont, 12);
        } catch (Exception e) {
            System.err.println("[PDFGenerator] Eroare la inițializarea fontului: " + e.getMessage());
            return null;
        }
    }

    private static void writeTicketDetails(Document document, Font font, String user, String reason, String staff, String timestamp) throws DocumentException {
        document.add(new Paragraph("Raport Tichet", font));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("👤 Utilizator: " + user, font));
        document.add(new Paragraph("❓ Motiv: " + reason, font));
        document.add(new Paragraph("🎯 Staff Asignat: " + staff, font));
        document.add(new Paragraph("🕒 Timestamp: " + timestamp, font));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("📂 Acest tichet a fost salvat pentru audit.", font));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("📜 Conversația completă:", font));
    }

    private static void writeChannelMessages(Document document, Font font, TextChannel channel) throws Exception {
        List<Message> messages = channel.getIterableHistory().takeAsync(MAX_MESSAGES).join();

        messages.sort(Comparator.comparing(ISnowflake::getTimeCreated));

        for (Message message : messages) {
            String formattedMessageTimestamp = message.getTimeCreated()
                    .toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            String messageContent = String.format("[%s] %s: %s",
                    formattedMessageTimestamp,
                    message.getAuthor().getName(),
                    message.getContentDisplay());

            document.add(new Paragraph(messageContent, font));

            for (Message.Attachment attachment : message.getAttachments()) {
                if (attachment.isImage()) {
                    File tempFile = new File("temp_" + attachment.getFileName());
                    attachment.downloadToFile(tempFile).join();

                    Image image = Image.getInstance(tempFile.getAbsolutePath());
                    image.scaleToFit(500, 500);
                    document.add(image);

                    tempFile.delete();
                } else {
                    String attachmentText = String.format("    ➤ [Atașament]: %s (%s)", attachment.getUrl(), attachment.getFileName());
                    document.add(new Paragraph(attachmentText, font));
                }
            }
        }
    }
}
