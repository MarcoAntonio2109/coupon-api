package br.com.onebrain.couponapi.exception;

import br.com.onebrain.couponapi.application.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * MAPEADOR DE EXCEÇÕES: Converte exceções de domínio em HTTP responses
 * 
 * Responsabilidade: Adaptar exceções agnósticas de tecnologia para o protocolo HTTP.
 * 
 * Exceções de domínio (application/) → HTTP Status Codes
 * - CouponNotFoundException → 404 Not Found
 * - InvalidCouponCodeException → 400 Bad Request
 * - InvalidExpirationDateException → 400 Bad Request
 * - CouponAlreadyExistsException → 409 Conflict
 * - CouponAlreadyDeletedException → 409 Conflict
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCouponNotFound(CouponNotFoundException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(CouponAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleCouponAlreadyExists(CouponAlreadyExistsException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(InvalidCouponCodeException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCode(InvalidCouponCodeException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(InvalidExpirationDateException.class)
    public ResponseEntity<Map<String, String>> handleInvalidDate(InvalidExpirationDateException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(CouponAlreadyDeletedException.class)
    public ResponseEntity<Map<String, String>> handleAlreadyDeleted(CouponAlreadyDeletedException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

}

