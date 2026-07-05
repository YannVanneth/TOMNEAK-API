package org.yannvanneth.tomneak.userservice.model.response;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * ApiResponseFactory class for creating ApiResponse objects.
 * @since 2026-06-21
 * */
@Component
@RequiredArgsConstructor
public class ApiResponseFactory{

    // Dependency Injection
    private final HttpServletRequest request;

    /**
     * Creates a success ApiResponse object.
     * @param payload the payload of the ApiResponse
     * @param message the message of the ApiResponse
     * @return ApiResponse<T>
     * */
    public <T> ApiResponse<T> success(T payload, String message) {
        return success(payload, message, HttpStatus.OK);
    }

    /**
     * Creates a created ApiResponse object.
     * @param payload the payload of the ApiResponse
     * @param message the message of the ApiResponse
     * @return ApiResponse<T>
     * */
    public <T> ApiResponse<T> created(T payload, String message){
        return success(payload, message, HttpStatus.CREATED);
    }

    /**
     * Creates a success ApiResponse object with a custom status.
     * @param payload the payload of the ApiResponse
     * @param message the message of the ApiResponse
     * @param status the status of the ApiResponse
     * @return ApiResponse<T>
     **/
    public <T> ApiResponse<T> success(T payload, String message, HttpStatus status) {
        return ApiResponse.<T>builder()
                .payload(payload)
                .message(message)
                .status(status)
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();
    }
}
