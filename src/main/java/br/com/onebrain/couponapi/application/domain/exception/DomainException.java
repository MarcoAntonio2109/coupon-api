package br.com.onebrain.couponapi.application.domain.exception;

/**
 * Exceção base do domínio.
 * Todas as exceções de negócio herdam desta.
 */
public abstract class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
