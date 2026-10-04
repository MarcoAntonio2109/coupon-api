package br.com.onebrain.couponapi.application.domain.exception;

/**
 * Exceção disparada quando cupom é deletado duas vezes.
 * Regra de negócio: Cupom já deletado não pode ser deletado novamente.
 */
public class CouponAlreadyDeletedException extends DomainException {
    public CouponAlreadyDeletedException() {
        super("Cupom já foi excluído.");
    }
}
