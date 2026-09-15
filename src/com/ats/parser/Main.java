package com.ats.parser;

import com.ats.entity.Candidate;
import java.io.File;

public class Main {

    public static void main(String[] args) {
        // Giả lập TextExtractor đọc nội dung file CV
        TextExtractor textExtractor = new TextExtractor() {
            @Override
            public String extractText(File file) {
                return """
                    HỌ VÀ TÊN: Nguyễn Văn A
                    Email: nguyenvana@gmail.com
                    
                    KỸ NĂNG CHUYÊN MÔN:
                    - Java, Spring Boot, Microservices
                    - MySQL, PostgreSQL, Redis
                    - Docker, Git, RESTful API
                    
                    DỰ ÁN THỰC TẾ:
                    - Tên dự án: Hệ thống ATS tự động bóc tách CV bằng AI
                      Mô tả: Sử dụng Gemini 1.5 Flash API để phân tích cú pháp CV ứng viên.
                    """;
            }
        };

        try {
            System.out.println("Đang đọc nội dung CV...");
            String rawText = textExtractor.extractText(null);

            System.out.println("Đang gửi dữ liệu tới Gemini AI để xử lý...");
            AiParser aiParser = new GeminiAiParser();
            Candidate candidate = aiParser.parseToCandidate(rawText);

            // In kết quả bóc tách thành công
            System.out.println("\n=== BÓC TÁCH KẾT QUẢ THÀNH CÔNG ===");
            System.out.println("Tên ứng viên     : " + candidate.getFullName());
            System.out.println("Email            : " + candidate.getEmail());
            System.out.println("Kỹ năng chuyên môn: " + candidate.getTechnicalSkills());
            System.out.println("Danh sách dự án  : " + candidate.getProjects());

        } catch (Exception e) {
            System.err.println("\n[LỖI QUY TRÌNH]: " + e.getMessage());
            e.printStackTrace();
        }
    }
}