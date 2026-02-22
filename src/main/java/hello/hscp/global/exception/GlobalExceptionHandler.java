// src/main/java/hello/hscp/global/exception/GlobalExceptionHandler.java
package hello.hscp.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handle(ApiException e, HttpServletRequest req) {
        // ApiException은 warn 한 줄만 (스택트레이스 X)
        log.warn("[{}] {} {} - {}", e.errorCode().code(), req.getMethod(), req.getRequestURI(), e.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", e.errorCode().code());
        body.put("message", e.getMessage());
        return ResponseEntity.status(e.errorCode().status()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handle(MethodArgumentNotValidException e, HttpServletRequest req) {
        log.warn("[{}] {} {} - Validation failed", ErrorCode.VALIDATION_ERROR.code(), req.getMethod(), req.getRequestURI());

        Map<String, Object> fields = new LinkedHashMap<>();
        for (FieldError fe : e.getBindingResult().getFieldErrors()) {
            fields.put(fe.getField(), fe.getDefaultMessage());
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", ErrorCode.VALIDATION_ERROR.code());
        body.put("message", "Validation failed");
        body.put("fields", fields);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            HttpMediaTypeNotSupportedException.class,
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<?> handleBadRequest(Exception e, HttpServletRequest req) {
        log.warn("[{}] {} {} - {}", ErrorCode.VALIDATION_ERROR.code(), req.getMethod(), req.getRequestURI(), e.getClass().getSimpleName());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", ErrorCode.VALIDATION_ERROR.code());
        body.put("message", "Bad request");
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handle(AccessDeniedException e, HttpServletRequest req) {
        log.warn("[{}] {} {} - Forbidden", ErrorCode.FORBIDDEN.code(), req.getMethod(), req.getRequestURI());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", ErrorCode.FORBIDDEN.code());
        body.put("message", "Forbidden");
        return ResponseEntity.status(ErrorCode.FORBIDDEN.status()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handle(Exception e, HttpServletRequest req) {
        // ERROR는 한 줄만, 스택트레이스는 DEBUG에서만
        log.error("[{}] {} {} - {}", ErrorCode.INTERNAL_SERVER_ERROR.code(), req.getMethod(), req.getRequestURI(), e.toString());
        log.debug("stacktrace", e);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", ErrorCode.INTERNAL_SERVER_ERROR.code());
        body.put("message", "Internal server error");
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.status()).body(body);
    }
}