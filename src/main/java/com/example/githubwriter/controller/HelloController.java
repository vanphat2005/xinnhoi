package com.example.githubwriter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Base64;

/**
 * Controller xử lý logic:
 *  - Trả về trang web giao diện (HTML)
 *  - Ghi file lên GitHub khi người dùng bấm nút
 */
@Controller
public class HelloController {

    @Value("${GITHUB_TOKEN}")
    private String token;

    @Value("${GITHUB_REPO}")
    private String repo;

    @Value("${GITHUB_USER}")
    private String user;


    /**
     * Hiển thị trang HTML chính (http://localhost:8080/hello)
     */
    @GetMapping("/hello")
    public String homePage() {
        return "frontend/screen"; // Spring Boot sẽ tự tìm file screen.html trong templates/frontend
    }

    /**
     * Xử lý khi người dùng bấm nút.
     * Ghi nội dung vào file trên GitHub qua API.
     */
    @ResponseBody
    @PostMapping("/click")
    public String writeFileToGit() {
        try {
            // Nội dung file
            String content = "Boss đã bấm nút vào lúc " + java.time.LocalDateTime.now();

            // GitHub yêu cầu mã hóa Base64
            String encoded = Base64.getEncoder().encodeToString(content.getBytes());

            // URL API GitHub
            String url = "https://api.github.com/repos/" + repo + "/contents/data.txt";


            // JSON payload
            String body = """
                {
                  "message": "Cập nhật file data.txt từ web",
                  "content": "%s"
                }
                """.formatted(encoded);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "token " + token);

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            // Gửi yêu cầu PUT đến GitHub
            RestTemplate rest = new RestTemplate();
            ResponseEntity<String> response = rest.exchange(url, HttpMethod.PUT, entity, String.class);

            return "✅ Đã ghi file lên GitHub: " + response.getStatusCode();
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Lỗi: " + e.getMessage();
        }
    }
}
