package com.ats.parser;
import java.io.File;

public interface TextExtractor {
    // Đầu vào là 1 file PDF, đầu ra là toàn bộ chữ trong file đó
    String extractText(File file);
}
