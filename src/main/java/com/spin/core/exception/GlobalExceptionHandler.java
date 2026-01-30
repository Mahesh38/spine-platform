package com.spin.core.exception;

import com.spin.core.audit.AuditEventType;
import com.spin.core.dto.ApiErrorResponse;
import com.spin.core.dto.ErrorCode;
import com.spin.core.dto.ProblemDetails;
import com.spin.core.service.AuditTrailService;
import com.spin.core.tenant.RequestContext;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@AllArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final AuditTrailService auditTrailService;
    private final HttpServletRequest request;


    // 404 - entity / commitment not found
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleNotFound(EntityNotFoundException ex) {
        logException(ex);
        return build(HttpStatus.NOT_FOUND, ErrorCode.SPINE_404_NOT_FOUND,ex.getMessage());
    }

    // 400 - bad input / illegal state
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadRequest(IllegalArgumentException ex) {
        logException(ex);
        return build(HttpStatus.BAD_REQUEST, ErrorCode.SPINE_400_INVALID_INPUT ,ex.getMessage());
    }

    // 400 - validation errors
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException ex) {
        logException(ex);
        return build(HttpStatus.BAD_REQUEST, ErrorCode.SPINE_400_INVALID_INPUT, ex.getMessage());
    }

    // 500 - fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        logException(ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.SPINE_500_INTERNAL,"Unexpected error occurred");
    }

    private void logException(Exception ex) {
        auditTrailService.log(
                AuditEventType.FAILED_OPERATION,
                null,
                null,
                "api",
                Map.of(
                        "path", request.getRequestURI(),
                        "method", request.getMethod(),
                        "errorType", ex.getClass().getSimpleName(),
                        "message", safeMessage(ex)
                )
        );

    }

    private String safeMessage(Exception ex) {
        if (ex instanceof IllegalArgumentException || ex instanceof IllegalStateException) {
            return ex.getMessage();
        }
        return "Unexpected error occurred";
    }

    private ResponseEntity<?> build(HttpStatus status, ErrorCode code, String message) {
        String requestId = RequestContext.getRequestId();
        String instance = request.getRequestURI();
        String accept = request.getHeader("Accept");

        if (accept != null && accept.contains("application/problem+json")) {
            ProblemDetails pd = ProblemDetails.builder()
                    .type("about:blank")
                    .title(status.getReasonPhrase())
                    .status(status.value())
                    .detail(message)
                    .instance(instance)
                    .requestId(requestId)
                    .code(code.name())
                    .build();

            return ResponseEntity.status(status)
                    .header("Content-Type", "application/problem+json")
                    .body(pd);
        }

        ApiErrorResponse body = ApiErrorResponse.builder()
                .requestId(requestId)
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .timestamp(java.time.Instant.now())
                .code(code.name())
                .build();

        return ResponseEntity.status(status).body(body);
    }

}
