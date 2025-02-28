package com.example.LibDev.review.filter;


import com.example.LibDev.global.exception.CustomErrorCode;
import com.example.LibDev.global.exception.CustomException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProfanityFilter {

    private static final String PROFANITY_API_URL = "https://api.profanity-filter.run/api/v1/filter";
    @Value("${profanity.api.key}")
    private String PROFANITY_API_KEY;

    private final RestTemplate restTemplate = new RestTemplate();

    public void checkProfanity(String text){
        // 요청 데이터 설정
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("text", text);
        requestBody.put("mode", "NORMAL");
        requestBody.put("callbackUrl", null);

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        headers.set("x-api-key", PROFANITY_API_KEY);

        // 요청 생성
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // API 호출
        ResponseEntity<Map> response = restTemplate.exchange(
                PROFANITY_API_URL, HttpMethod.POST, requestEntity, Map.class
        );

        // 응답 처리
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            Object detected = body.get("detected");

            if (detected != null && detected.toString().length() > 2) { // 비속어 감지 시
                throw new CustomException(CustomErrorCode.PROFANITY_DETECTED);
            }
        } else {
            throw new CustomException(CustomErrorCode.PROFANITY_FILTER_SERVICE_ERROR);
        }
    }
}
