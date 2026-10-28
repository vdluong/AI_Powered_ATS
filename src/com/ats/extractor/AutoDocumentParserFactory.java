package com.ats.extractor;

import java.io.File;

public class AutoDocumentParserFactory extends DocumentParserFactory {

    @Override
    public DocumentParser createParser(File file) {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File không tồn tại!");
        }

        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".pdf")) {
            return new PdfParser();
        } else if (fileName.endsWith(".docx")) {
            return new DocxParser();
        } else {
            throw new UnsupportedOperationException(
                "Định dạng file không được hỗ trợ. Hệ thống chỉ hỗ trợ .pdf hoặc .docx"
            );
        }
    }
}
