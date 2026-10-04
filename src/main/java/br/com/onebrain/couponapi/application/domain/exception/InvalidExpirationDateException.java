package br.com.onebrain.couponapi.application.domain.exception;

import java.time.LocalDate;

/**
 * Exceção disparada quando data de expiração é inválida.
 * Regra de negócio: Data não pode estar no passado.
 */
public class InvalidExpirationDateException extends DomainException {
    public InvalidExpirationDateException(LocalDate date) {
        super(String.format(
            "Data de expiração '%s' inválida. Não pode estar no passado.",
            date
        ));
    }
}
