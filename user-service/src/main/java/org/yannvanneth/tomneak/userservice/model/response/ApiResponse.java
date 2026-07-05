package org.yannvanneth.tomneak.userservice.model.response;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
public class ApiResponse<T> {
    private T payload;
    private String message;
    private HttpStatus status;
    private Instant timestamp;
    private String path;
}

