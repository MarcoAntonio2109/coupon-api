package br.com.onebrain.couponapi.application.domain.exception;

/**
 * Exceção disparada quando cupom duplicado é detectado.
 * Regra de negócio: Códigos devem ser únicos.
 */
public class CouponAlreadyExistsException extends DomainException {
    public CouponAlreadyExistsException(String code) {
        super(String.format("Cupom com código '%s' já existe.", code));
    }
}
