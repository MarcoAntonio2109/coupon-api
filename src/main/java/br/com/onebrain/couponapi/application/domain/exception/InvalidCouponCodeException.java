package br.com.onebrain.couponapi.application.domain.exception;

/**
 * Exceção disparada quando código é inválido.
 * Regra de negócio: Código deve ter exatamente 6 caracteres alfanuméricos.
 */
public class InvalidCouponCodeException extends DomainException {
    public InvalidCouponCodeException(String code) {
        super(String.format(
            "Código '%s' inválido. Deve ter exatamente 6 caracteres alfanuméricos após a remoção de caracteres especiais.",
            code
        ));
    }
}
