package com.ats.extractor;

import java.io.File;
import java.io.IOException;

public interface DocumentParser {
    /**
     * Bóc tách toàn bộ nội dung text từ file
     * @param file File cần bóc tách
     * @return Chuỗi văn bản thô đã chuẩn hóa
     * @throws IOException Nếu xảy ra lỗi đọc file
     */
    String extractText(File file) throws IOException;
}
