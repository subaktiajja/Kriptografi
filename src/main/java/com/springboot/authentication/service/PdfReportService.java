package com.springboot.authentication.service;

import com.springboot.authentication.entity.JadwalUas;
import com.springboot.authentication.entity.User;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class PdfReportService {

    private static final PDType1Font FONT_NORMAL =
            new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDType1Font FONT_BOLD =
            new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    private static final float PAGE_MARGIN = 46f;
    private static final float ROW_HEIGHT = 24f;
    private static final float CELL_FONT_SIZE = 9f;

    private static final String SCHOOL_NAME = "MTsN 22 Jakarta";
    private static final String ADDRESS_LINE_1 =
            "Buni No.81 Rt.003/05, Kec. Cipayung, Kota Adm. Jakarta Timur,";
    private static final String ADDRESS_LINE_2 = "Prov. D.K.I. Jakarta";
    private static final String HEADMASTER_NAME = "SYARIF CECEP, S.T.";

    /**
     * Membuat laporan Data User dengan tampilan kop, tabel, kalimat penutup,
     * dan tanda tangan Kepala Madrasah di sebelah kanan.
     */
    public byte[] generateUserReport(List<User> userList) throws IOException {
        List<String> headers = List.of("No", "Nama User", "Username", "Email", "Status");
        List<List<String>> rows = new ArrayList<>();

        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String fullName = (safe(user.getFirstName()) + " " + safe(user.getLastName())).trim();

            rows.add(List.of(
                    String.valueOf(i + 1),
                    fullName,
                    safe(user.getUsername()),
                    safe(user.getEmail()),
                    user.isEnabled() ? "Aktif" : "Tidak Aktif"
            ));
        }

        return createReport(
                "LAPORAN DATA USER",
                headers,
                rows,
                new float[]{40f, 150f, 110f, 260f, 85f}
        );
    }

    /**
     * Membuat laporan Jadwal UAS dengan desain yang sama seperti laporan Data User.
     */
    public byte[] generateJadwalReport(List<JadwalUas> jadwalList) throws IOException {
        List<String> headers = List.of("No", "Hari", "Mata Ujian", "Kelas", "Jam", "Tempat");
        List<List<String>> rows = new ArrayList<>();

        for (int i = 0; i < jadwalList.size(); i++) {
            JadwalUas jadwal = jadwalList.get(i);
            rows.add(List.of(
                    String.valueOf(i + 1),
                    safe(jadwal.getHari()),
                    safe(jadwal.getMataPelajaran()),
                    safe(jadwal.getKelas()),
                    safe(jadwal.getJam()),
                    safe(jadwal.getTempat())
            ));
        }

        return createReport(
                "LAPORAN JADWAL UAS",
                headers,
                rows,
                new float[]{38f, 90f, 210f, 120f, 135f, 165f}
        );
    }

    private byte[] createReport(
            String title,
            List<String> headers,
            List<List<String>> rows,
            float[] columnWidths) throws IOException {

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            addReportPages(document, title, headers, rows, columnWidths);
            document.save(output);
            return output.toByteArray();
        }
    }

    private void addReportPages(
            PDDocument document,
            String title,
            List<String> headers,
            List<List<String>> rows,
            float[] columnWidths) throws IOException {

        // Dibuat landscape agar tampilannya menyerupai desain laporan acuan
        // dan tabel memiliki ruang yang cukup saat dicetak.
        PDRectangle pageSize = new PDRectangle(
                PDRectangle.A4.getHeight(),
                PDRectangle.A4.getWidth()
        );

        PageState state = createReportPage(
                document,
                pageSize,
                title,
                headers,
                columnWidths,
                false
        );

        if (rows.isEmpty()) {
            drawText(
                    state.contentStream,
                    "Belum ada data yang tersimpan.",
                    FONT_NORMAL,
                    10f,
                    state.tableX,
                    state.yPosition - 8f
            );
            state.yPosition -= 38f;
        } else {
            for (List<String> row : rows) {
                if (state.yPosition - ROW_HEIGHT < 175f) {
                    state.contentStream.close();
                    state = createReportPage(
                            document,
                            pageSize,
                            title,
                            headers,
                            columnWidths,
                            true
                    );
                }

                drawRow(
                        state.contentStream,
                        state.tableX,
                        state.yPosition,
                        row,
                        columnWidths,
                        false
                );
                state.yPosition -= ROW_HEIGHT;
            }
        }

        // Pastikan halaman terakhir tetap memiliki ruang untuk penutup dan tanda tangan.
        if (state.yPosition < 210f) {
            state.contentStream.close();
            state = createClosingPage(document, pageSize, title);
        }

        drawClosingAndSignature(state.contentStream, pageSize, state.yPosition);
        state.contentStream.close();
    }

    private PageState createReportPage(
            PDDocument document,
            PDRectangle pageSize,
            String title,
            List<String> headers,
            float[] columnWidths,
            boolean continuation) throws IOException {

        PDPage page = new PDPage(pageSize);
        document.addPage(page);

        PDPageContentStream content = new PDPageContentStream(document, page);
        drawLetterhead(document, content, pageSize);

        float titleY = pageSize.getHeight() - 190f;
        String displayTitle = continuation ? title + " (LANJUTAN)" : title;
        drawCenteredText(content, displayTitle, FONT_BOLD, 16f, pageSize.getWidth(), titleY);

        float totalTableWidth = sum(columnWidths);
        float tableX = (pageSize.getWidth() - totalTableWidth) / 2f;
        float tableTop = titleY - 52f;

        drawRow(content, tableX, tableTop, headers, columnWidths, true);

        return new PageState(content, tableTop - ROW_HEIGHT, tableX);
    }

    private PageState createClosingPage(
            PDDocument document,
            PDRectangle pageSize,
            String title) throws IOException {

        PDPage page = new PDPage(pageSize);
        document.addPage(page);

        PDPageContentStream content = new PDPageContentStream(document, page);
        drawLetterhead(document, content, pageSize);

        float titleY = pageSize.getHeight() - 190f;
        drawCenteredText(content, title + " (LANJUTAN)", FONT_BOLD, 16f, pageSize.getWidth(), titleY);

        return new PageState(content, titleY - 55f, PAGE_MARGIN);
    }

    private void drawLetterhead(
            PDDocument document,
            PDPageContentStream content,
            PDRectangle pageSize) throws IOException {

        float pageHeight = pageSize.getHeight();
        float pageWidth = pageSize.getWidth();

        // Logo di kiri atas, mengikuti desain laporan acuan pengguna.
        float logoWidth = 105f;
        float logoHeight = 105f;
        float logoX = 70f;
        float logoY = pageHeight - 145f;

        // Logo bersifat opsional. Jika file logo tidak ditemukan atau rusak,
        // laporan tetap dibuat agar tombol Cetak PDF tidak menghasilkan error.
        ClassPathResource logoResource = new ClassPathResource("static/gambar/logo-mtsn22.jpg");
        if (logoResource.exists()) {
            try (InputStream input = logoResource.getInputStream()) {
                PDImageXObject logo = PDImageXObject.createFromByteArray(
                        document,
                        input.readAllBytes(),
                        "logo-mtsn22"
                );
                content.drawImage(logo, logoX, logoY, logoWidth, logoHeight);
            } catch (IOException | RuntimeException ignored) {
                // Lanjutkan pembuatan PDF tanpa logo.
            }
        }

        // Area teks kop dipusatkan pada bagian kanan logo.
        float textAreaX = 210f;
        float textAreaWidth = pageWidth - textAreaX - 70f;
        float schoolY = pageHeight - 70f;

        drawCenteredTextInArea(
                content,
                SCHOOL_NAME,
                FONT_BOLD,
                20f,
                textAreaX,
                textAreaWidth,
                schoolY
        );

        drawCenteredTextInArea(
                content,
                ADDRESS_LINE_1,
                FONT_NORMAL,
                11f,
                textAreaX,
                textAreaWidth,
                schoolY - 48f
        );

        drawCenteredTextInArea(
                content,
                ADDRESS_LINE_2,
                FONT_NORMAL,
                11f,
                textAreaX,
                textAreaWidth,
                schoolY - 66f
        );

        float lineY = pageHeight - 160f;
        content.setLineWidth(1.8f);
        content.moveTo(60f, lineY);
        content.lineTo(pageWidth - 60f, lineY);
        content.stroke();
    }

    private void drawClosingAndSignature(
            PDPageContentStream content,
            PDRectangle pageSize,
            float yPosition) throws IOException {

        float closingY = yPosition - 50f;
        drawText(
                content,
                "Demikian laporan ini dibuat untuk dapat dipergunakan sebagaimana mestinya.",
                FONT_NORMAL,
                11f,
                70f,
                closingY
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "dd MMMM yyyy",
                Locale.forLanguageTag("id-ID")
        );
        String dateText = "Jakarta, " + LocalDate.now().format(formatter);

        // Hanya satu blok tanda tangan: Kepala Madrasah di sebelah kanan.
        float signatureX = pageSize.getWidth() - 270f;
        float signatureY = closingY - 35f;

        drawText(content, dateText, FONT_NORMAL, 11f, signatureX, signatureY);
        drawText(content, "Mengetahui,", FONT_NORMAL, 11f, signatureX, signatureY - 20f);
        drawText(content, "Kepala Madrasah", FONT_NORMAL, 11f, signatureX, signatureY - 40f);

        float lineY = signatureY - 125f;
        content.setLineWidth(0.8f);
        content.moveTo(signatureX, lineY);
        content.lineTo(signatureX + 175f, lineY);
        content.stroke();

        drawText(
                content,
                HEADMASTER_NAME,
                FONT_BOLD,
                11f,
                signatureX,
                lineY - 20f
        );
    }

    private void drawRow(
            PDPageContentStream content,
            float startX,
            float y,
            List<String> cells,
            float[] widths,
            boolean header) throws IOException {

        float totalWidth = sum(widths);

        if (header) {
            content.setNonStrokingColor(242f / 255f, 242f / 255f, 242f / 255f);
            content.addRect(startX, y - ROW_HEIGHT, totalWidth, ROW_HEIGHT);
            content.fill();
            content.setNonStrokingColor(0f, 0f, 0f);
        }

        content.setLineWidth(0.6f);
        content.addRect(startX, y - ROW_HEIGHT, totalWidth, ROW_HEIGHT);
        content.stroke();

        float x = startX;
        for (int i = 0; i < widths.length; i++) {
            if (i > 0) {
                content.moveTo(x, y);
                content.lineTo(x, y - ROW_HEIGHT);
                content.stroke();
            }

            String value = i < cells.size() ? safe(cells.get(i)) : "";
            PDType1Font font = header ? FONT_BOLD : FONT_NORMAL;
            String fitted = fitText(value, font, CELL_FONT_SIZE, widths[i] - 10f);

            drawText(
                    content,
                    fitted,
                    font,
                    CELL_FONT_SIZE,
                    x + 5f,
                    y - 16f
            );

            x += widths[i];
        }
    }

    private void drawCenteredText(
            PDPageContentStream content,
            String text,
            PDType1Font font,
            float fontSize,
            float pageWidth,
            float y) throws IOException {

        float textWidth = font.getStringWidth(sanitizeForPdf(text)) / 1000f * fontSize;
        float x = (pageWidth - textWidth) / 2f;
        drawText(content, text, font, fontSize, x, y);
    }

    private void drawCenteredTextInArea(
            PDPageContentStream content,
            String text,
            PDType1Font font,
            float fontSize,
            float areaX,
            float areaWidth,
            float y) throws IOException {

        float textWidth = font.getStringWidth(sanitizeForPdf(text)) / 1000f * fontSize;
        float x = areaX + (areaWidth - textWidth) / 2f;
        drawText(content, text, font, fontSize, x, y);
    }

    private void drawText(
            PDPageContentStream content,
            String text,
            PDType1Font font,
            float fontSize,
            float x,
            float y) throws IOException {

        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, y);
        content.showText(sanitizeForPdf(text));
        content.endText();
    }

    private String fitText(
            String text,
            PDType1Font font,
            float fontSize,
            float maxWidth) throws IOException {

        String value = sanitizeForPdf(safe(text));
        if (font.getStringWidth(value) / 1000f * fontSize <= maxWidth) {
            return value;
        }

        String suffix = "...";
        String shortened = value;

        while (!shortened.isEmpty()) {
            shortened = shortened.substring(0, shortened.length() - 1);
            String candidate = shortened + suffix;

            if (font.getStringWidth(candidate) / 1000f * fontSize <= maxWidth) {
                return candidate;
            }
        }

        return suffix;
    }

    private float sum(float[] values) {
        float total = 0f;
        for (float value : values) {
            total += value;
        }
        return total;
    }

    private String sanitizeForPdf(String text) {
        return text
                .replace('\n', ' ')
                .replace('\r', ' ')
                .replace('\t', ' ')
                .replace('\u2013', '-')
                .replace('\u2014', '-')
                .replace('\u2018', '\'')
                .replace('\u2019', '\'')
                .replace('\u201c', '"')
                .replace('\u201d', '"');
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private static class PageState {
        private final PDPageContentStream contentStream;
        private float yPosition;
        private final float tableX;

        private PageState(
                PDPageContentStream contentStream,
                float yPosition,
                float tableX) {
            this.contentStream = contentStream;
            this.yPosition = yPosition;
            this.tableX = tableX;
        }
    }
}
