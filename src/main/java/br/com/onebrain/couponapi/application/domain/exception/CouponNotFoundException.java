package br.com.onebrain.couponapi.application.domain.exception;

/**
 * Exceção disparada quando cupom não é encontrado.
 * Regra de negócio: Operação requer cupom existente.
 */
public class CouponNotFoundException extends DomainException {
    public CouponNotFoundException(String code) {
        super(String.format("Cupom com código '%s' não encontrado.", code));
    }
}
