package com.ats.parser;

import com.ats.entity.Candidate;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Set;

public class GeminiAiParser implements AiParser {

    private final String apiKey;
    private final HttpClient httpClient;
    private final Gson gson;

    // =========================================================
    // CHỈ DÁN API KEY Ở ĐÂY
    // =========================================================
    public GeminiAiParser() {
        this("DÁN API");
    }

    public GeminiAiParser(String apiKey) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException(
                    "API Key không được để trống!"
            );
        }

        this.apiKey = apiKey.trim();

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        this.gson = new Gson();
    }

    // =========================================================
    // HÀM CHÍNH PHÂN TÍCH CV
    // =========================================================
    @Override
    public Candidate parseToCandidate(String rawText) {

        if (rawText == null || rawText.isBlank()) {
            throw new IllegalArgumentException(
                    "Nội dung CV không được để trống!"
            );
        }

        System.out.println();
        System.out.println("==========================================");
        System.out.println("        AI POWERED ATS - GEMINI");
        System.out.println("==========================================");
        System.out.println("Model      : Gemini 3.5 Flash");
        System.out.println("Trạng thái : Đang phân tích CV...");
        System.out.println("==========================================");

        try {

            String prompt = buildPrompt(rawText);
            String requestJson = buildRequestBody(prompt);

            String endpoint =
                    "https://generativelanguage.googleapis.com/v1beta/models/"
                            + "gemini-3.5-flash:generateContent";

            int soLanThuToiDa = 3;

            for (int lan = 1; lan <= soLanThuToiDa; lan++) {

                System.out.println(
                        "\n[Gemini] Đang gửi CV... "
                                + "Lần "
                                + lan
                                + "/"
                                + soLanThuToiDa
                );

                HttpRequest request =
                        HttpRequest.newBuilder()
                                .uri(URI.create(endpoint))
                                .header(
                                        "Content-Type",
                                        "application/json"
                                )
                                .header(
                                        "x-goog-api-key",
                                        apiKey
                                )
                                .POST(
                                        HttpRequest.BodyPublishers
                                                .ofString(requestJson)
                                )
                                .timeout(
                                        Duration.ofSeconds(60)
                                )
                                .build();

                HttpResponse<String> response =
                        httpClient.send(
                                request,
                                HttpResponse.BodyHandlers.ofString()
                        );

                int statusCode =
                        response.statusCode();

                // =================================================
                // THÀNH CÔNG
                // =================================================
                if (statusCode == 200) {

                    System.out.println(
                            "[Gemini] ✓ Kết nối thành công!"
                    );

                    String jsonResult =
                            extractJsonFromResponse(
                                    response.body()
                            );

                    System.out.println(
                            "[Gemini] ✓ JSON hợp lệ!"
                    );

                    jsonResult =
                            normalizeJson(jsonResult);

                    Candidate candidate =
                            gson.fromJson(
                                    jsonResult,
                                    Candidate.class
                            );

                    if (candidate == null) {
                        throw new RuntimeException(
                                "Không thể tạo Candidate từ JSON."
                        );
                    }

                    System.out.println(
                            "[Gemini] ✓ Bóc tách CV thành công!"
                    );

                    System.out.println(
                            "=========================================="
                    );

                    return candidate;
                }

                // =================================================
                // 429 - QUÁ NHIỀU REQUEST
                // =================================================
                if (statusCode == 429) {

                    System.err.println(
                            "[Gemini] ⚠ API đang giới hạn request (429)."
                    );

                    if (lan < soLanThuToiDa) {

                        choVaThuLai(lan, 10);

                        continue;
                    }

                    throw new RuntimeException(
                            "Gemini đã vượt giới hạn request."
                    );
                }

                // =================================================
                // 500 / 502 / 503 / 504
                // =================================================
                if (statusCode == 500 ||
                        statusCode == 502 ||
                        statusCode == 503 ||
                        statusCode == 504) {

                    System.err.println(
                            "[Gemini] ⚠ Máy chủ Gemini đang bận."
                    );

                    System.err.println(
                            "HTTP Code: " + statusCode
                    );

                    if (lan < soLanThuToiDa) {

                        choVaThuLai(lan, 5);

                        continue;
                    }

                    throw new RuntimeException(
                            "Gemini không thể xử lý request sau "
                                    + soLanThuToiDa
                                    + " lần thử."
                    );
                }

                // =================================================
                // API KEY SAI
                // =================================================
                if (statusCode == 400 ||
                        statusCode == 401 ||
                        statusCode == 403) {

                    System.err.println(
                            "=========================================="
                    );

                    System.err.println(
                            "[Gemini] ✗ LỖI API KEY / QUYỀN TRUY CẬP"
                    );

                    System.err.println(
                            "HTTP Code: " + statusCode
                    );

                    System.err.println(
                            response.body()
                    );

                    System.err.println(
                            "=========================================="
                    );

                    throw new RuntimeException(
                            "API key không hợp lệ hoặc "
                                    + "không có quyền sử dụng model."
                    );
                }

                // =================================================
                // MODEL KHÔNG TỒN TẠI
                // =================================================
                if (statusCode == 404) {

                    System.err.println(
                            "=========================================="
                    );

                    System.err.println(
                            "[Gemini] ✗ KHÔNG TÌM THẤY MODEL"
                    );

                    System.err.println(
                            "HTTP Code: 404"
                    );

                    System.err.println(
                            response.body()
                    );

                    System.err.println(
                            "=========================================="
                    );

                    throw new RuntimeException(
                            "Không tìm thấy model Gemini 3.5 Flash "
                                    + "hoặc model không khả dụng với API key."
                    );
                }

                // =================================================
                // LỖI KHÁC
                // =================================================
                System.err.println(
                        "=========================================="
                );

                System.err.println(
                        "[Gemini] ✗ API ERROR"
                );

                System.err.println(
                        "HTTP Code: " + statusCode
                );

                System.err.println(
                        response.body()
                );

                System.err.println(
                        "=========================================="
                );

                throw new RuntimeException(
                        "Gemini API báo lỗi HTTP: "
                                + statusCode
                );
            }

            throw new RuntimeException(
                    "Không thể phân tích CV."
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Quá trình chờ Gemini bị gián đoạn.",
                    e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Lỗi bóc tách CV tại GeminiAiParser: "
                            + e.getMessage(),
                    e
            );
        }
    }

    // =========================================================
    // CHỜ RỒI THỬ LẠI
    // =========================================================
    private void choVaThuLai(
            int lan,
            int soGiayCoBan) throws InterruptedException {

        int soGiay =
                lan * soGiayCoBan;

        System.out.println(
                "[Gemini] Chờ "
                        + soGiay
                        + " giây rồi thử lại..."
        );

        Thread.sleep(
                soGiay * 1000L
        );
    }

    // =========================================================
    // PROMPT ATS CHUYÊN NGHIỆP
    // =========================================================
    private String buildPrompt(String rawText) {

        return """
                Bạn là một hệ thống ATS (Applicant Tracking System)
                chuyên nghiệp dùng để phân tích CV ứng viên.

                NHIỆM VỤ:
                Đọc toàn bộ CV và trích xuất thông tin thành JSON.

                CẤU TRÚC JSON BẮT BUỘC:

                {
                  "fullName": "",
                  "email": "",
                  "technicalSkills": [],
                  "projects": [
                    {
                      "name": "",
                      "technologies": [],
                      "description": ""
                    }
                  ]
                }

                QUY TẮC PHÂN TÍCH:

                1. fullName
                - Lấy chính xác họ tên trong CV.
                - Không tự đoán.
                - Không thêm chức danh vào họ tên.

                2. email
                - Lấy email xuất hiện trong CV.
                - Không tự tạo email.
                - Không có thì trả về "".

                3. technicalSkills
                - Chỉ lấy kỹ năng kỹ thuật thực sự xuất hiện.
                - Ví dụ: Java, Python, C++, SQL, Docker...
                - Không tự thêm kỹ năng.
                - Không lặp kỹ năng.
                - Giữ tên kỹ năng rõ ràng.

                4. projects
                - Tìm tất cả dự án được đề cập trong CV.
                - Mỗi dự án phải có:
                  + name
                  + technologies
                  + description

                5. technologies
                - Chỉ đưa công nghệ thực sự liên quan
                  đến dự án đó.
                - Không tự đoán công nghệ.

                6. description
                - Tóm tắt dự án dựa hoàn toàn
                  trên thông tin trong CV.
                - Không bịa thành tích.
                - Không bịa số liệu.

                7. Nếu CV không có dự án:
                   "projects": []

                8. Nếu không tìm thấy:
                   String -> ""
                   Array -> []

                9. Nếu thông tin không chắc chắn:
                   Không được tự bịa.

                10. KẾT QUẢ:
                    CHỈ TRẢ VỀ JSON THUẦN.

                Không giải thích.
                Không viết markdown.
                Không dùng ```json.
                Không dùng ```.

                CV:
                ==================================================
                """ + rawText;
    }

    // =========================================================
    // TẠO REQUEST JSON
    // =========================================================
    private String buildRequestBody(String prompt) {

        JsonObject textPart =
                new JsonObject();

        textPart.addProperty(
                "text",
                prompt
        );

        JsonArray parts =
                new JsonArray();

        parts.add(
                textPart
        );

        JsonObject content =
                new JsonObject();

        content.add(
                "parts",
                parts
        );

        JsonArray contents =
                new JsonArray();

        contents.add(
                content
        );

        JsonObject generationConfig =
                new JsonObject();

        // Gemini phải trả về JSON
        generationConfig.addProperty(
                "responseMimeType",
                "application/json"
        );

        JsonObject root =
                new JsonObject();

        root.add(
                "contents",
                contents
        );

        root.add(
                "generationConfig",
                generationConfig
        );

        return gson.toJson(root);
    }

    // =========================================================
    // LẤY JSON TỪ GEMINI
    // =========================================================
    private String extractJsonFromResponse(
            String responseBody) {

        if (responseBody == null ||
                responseBody.isBlank()) {

            throw new RuntimeException(
                    "Gemini trả về response rỗng."
            );
        }

        JsonObject root;

        try {

            root =
                    JsonParser.parseString(
                            responseBody
                    ).getAsJsonObject();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Response Gemini không phải JSON hợp lệ."
            );
        }

        // Kiểm tra candidates
        if (!root.has("candidates")) {

            throw new RuntimeException(
                    "Gemini không trả về candidates."
            );
        }

        JsonArray candidates =
                root.getAsJsonArray(
                        "candidates"
                );

        if (candidates == null ||
                candidates.size() == 0) {

            throw new RuntimeException(
                    "Gemini trả về candidates rỗng."
            );
        }

        JsonObject candidate =
                candidates
                        .get(0)
                        .getAsJsonObject();

        // Kiểm tra content
        if (!candidate.has("content")) {

            throw new RuntimeException(
                    "Gemini không trả về content."
            );
        }

        JsonObject content =
                candidate.getAsJsonObject(
                        "content"
                );

        // Kiểm tra parts
        if (!content.has("parts")) {

            throw new RuntimeException(
                    "Gemini không trả về parts."
            );
        }

        JsonArray parts =
                content.getAsJsonArray(
                        "parts"
                );

        if (parts.size() == 0) {

            throw new RuntimeException(
                    "Gemini trả về parts rỗng."
            );
        }

        JsonObject part =
                parts
                        .get(0)
                        .getAsJsonObject();

        // Kiểm tra text
        if (!part.has("text")) {

            throw new RuntimeException(
                    "Gemini không trả về text."
            );
        }

        String text =
                part
                        .get("text")
                        .getAsString();

        if (text == null ||
                text.isBlank()) {

            throw new RuntimeException(
                    "Gemini trả về text rỗng."
            );
        }

        return text.trim();
    }

    // =========================================================
    // CHUẨN HÓA JSON
    // =========================================================
    private String normalizeJson(
            String json) {

        json = json
                .replace("```json", "")
                .replace("```", "")
                .trim();

        JsonObject root;

        try {

            root =
                    JsonParser
                            .parseString(json)
                            .getAsJsonObject();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Gemini trả về JSON không hợp lệ:\n"
                            + json
            );
        }

        // -----------------------------------------------------
        // Đảm bảo fullName tồn tại
        // -----------------------------------------------------
        if (!root.has("fullName") ||
                root.get("fullName").isJsonNull()) {

            root.addProperty(
                    "fullName",
                    ""
            );
        }

        // -----------------------------------------------------
        // Đảm bảo email tồn tại
        // -----------------------------------------------------
        if (!root.has("email") ||
                root.get("email").isJsonNull()) {

            root.addProperty(
                    "email",
                    ""
            );
        }

        // -----------------------------------------------------
        // Đảm bảo technicalSkills tồn tại
        // -----------------------------------------------------
        if (!root.has("technicalSkills") ||
                root.get("technicalSkills").isJsonNull()) {

            root.add(
                    "technicalSkills",
                    new JsonArray()
            );
        }

        // -----------------------------------------------------
        // Đảm bảo projects tồn tại
        // -----------------------------------------------------
        if (!root.has("projects") ||
                root.get("projects").isJsonNull()) {

            root.add(
                    "projects",
                    new JsonArray()
            );
        }

        // -----------------------------------------------------
        // Chuẩn hóa kỹ năng
        // -----------------------------------------------------
        JsonArray skills =
                root.getAsJsonArray(
                        "technicalSkills"
                );

        Set<String> skillsKhongTrung =
                new LinkedHashSet<>();

        for (JsonElement element : skills) {

            if (element.isJsonPrimitive()) {

                String skill =
                        element
                                .getAsString()
                                .trim();

                if (!skill.isEmpty()) {
                    skillsKhongTrung.add(skill);
                }
            }
        }

        JsonArray skillsMoi =
                new JsonArray();

        for (String skill :
                skillsKhongTrung) {

            skillsMoi.add(skill);
        }

        root.add(
                "technicalSkills",
                skillsMoi
        );

        // -----------------------------------------------------
        // Chuẩn hóa projects
        // -----------------------------------------------------
        JsonArray projects =
                root.getAsJsonArray(
                        "projects"
                );

        JsonArray projectsMoi =
                new JsonArray();

        for (JsonElement element :
                projects) {

            if (!element.isJsonObject()) {
                continue;
            }

            JsonObject project =
                    element.getAsJsonObject();

            if (!project.has("name") ||
                    project.get("name").isJsonNull()) {

                project.addProperty(
                        "name",
                        ""
                );
            }

            if (!project.has("technologies") ||
                    project.get("technologies").isJsonNull()) {

                project.add(
                        "technologies",
                        new JsonArray()
                );
            }

            if (!project.has("description") ||
                    project.get("description").isJsonNull()) {

                project.addProperty(
                        "description",
                        ""
                );
            }

            projectsMoi.add(project);
        }

        root.add(
                "projects",
                projectsMoi
        );

        return gson.toJson(root);
    }
}