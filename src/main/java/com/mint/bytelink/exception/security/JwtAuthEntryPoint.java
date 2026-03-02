package com.mint.bytelink.exception.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(401);
        response.setContentType("application/json");

        String message = authException.getMessage();

        response.getWriter().write(
                """
                {
                  "status": 401,
                  "error": "Unauthorized",
                  "message": "%s",
                  "path": "%s"
                }
                """.formatted(message , request.getRequestURI())
        );
    }
}
