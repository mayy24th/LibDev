package com.example.LibDev.global.config;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class ErrorControllerImpl implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        // 현재 요청된 URL에서 '/error'를 제외한 도메인 부분만 추출
        String serverUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        model.addAttribute("serverUrl", serverUrl);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            log.error("Error occurred with status code: {}", statusCode);

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "global/error-404"; // global/error-404.html 반환
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "global/error-403"; // global/error-403.html 반환
            }
        } else {
            log.error("An unknown error occurred");
        }

        return "global/error"; // global/error.html 반환
    }
}

