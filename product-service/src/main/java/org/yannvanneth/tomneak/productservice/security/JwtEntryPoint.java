package org.yannvanneth.tomneak.productservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * AuthenticationEntryPoint implementation that handles unauthenticated request failures with JSON error responses.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Component
@RequiredArgsConstructor
public class JwtEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /**
     * Commences an authentication scheme when an AuthenticationException occurs.
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @param authException exception that caused the invocation
     * @throws IOException in case of I/O errors
     * @throws ServletException in case of servlet errors
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorDetails.put("error", "Unauthorized");
        errorDetails.put("message", "Full authentication is required to access this resource");
        errorDetails.put("path", request.getServletPath());

        objectMapper.writeValue(response.getOutputStream(), errorDetails);
    }
}
