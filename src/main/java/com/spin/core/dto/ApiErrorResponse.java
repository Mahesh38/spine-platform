package com.spin.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class ApiErrorResponse {

    private String requestId;
    private int status;
    private String error;
    private String code;
    private String message;
    private Instant timestamp;
}
