package com.ats.extractor;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PdfParser implements DocumentParser {

    @Override
    public String extractText(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            // Kiểm tra file có bị đặt mật khẩu bảo vệ hay không
            if (document.isEncrypted()) {
                throw new IOException("File PDF đang bị khóa mật khẩu: " + file.getName());
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true); // Sắp xếp lại thứ tự chữ theo tọa độ trực quan trên trang
            String rawText = stripper.getText(document);

            return TextNormalizer.cleanVietnameseText(rawText);
        }
    }
}
