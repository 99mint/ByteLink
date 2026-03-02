package com.mint.bytelink.exception.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(403);
        response.setContentType("application/json");

        String message = accessDeniedException.getMessage();

        response.getWriter().write(
                """
                {
                  "status": 403,
                  "error": "Forbidden",
                  "message": "%s",
                  "path": "%s"
                }
                """.formatted(message , request.getRequestURI())
        );
    }
}
