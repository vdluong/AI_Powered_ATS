package com.ats.extractor;

import java.text.Normalizer;

public class TextNormalizer {

    public static String cleanVietnameseText(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return "";
        }

        // 1. Chuẩn hóa về Unicode NFC (chuẩn Unicode tiếng Việt phổ thông)
        String normalized = Normalizer.normalize(rawText, Normalizer.Form.NFC);

        // 2. Thay thế khoảng trắng không ngắt và ký tự điều khiển
        normalized = normalized.replace('\u00A0', ' ')
                               .replace('\uFEFF', ' ');

        // 3. Gom nhiều dòng trống thành một dòng trống duy nhất
        normalized = normalized.replaceAll("(?m)^[ \t]*\r?\n", "\n");

        // 4. Gom nhiều khoảng trắng liền nhau thành 1 khoảng trắng
        normalized = normalized.replaceAll("[ \t]+", " ");

        return normalized.trim();
    }
}