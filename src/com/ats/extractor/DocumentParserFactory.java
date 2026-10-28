package com.ats.extractor;

import java.io.File;

public abstract class DocumentParserFactory {
    public abstract DocumentParser createParser(File file);

    /**
     * Phương thức tiện ích bóc tách trực tiếp văn bản từ file thông qua Factory Method
     */
    public String parseDocument(File file) throws Exception {
        DocumentParser parser = createParser(file);
        return parser.extractText(file);
    }
}
