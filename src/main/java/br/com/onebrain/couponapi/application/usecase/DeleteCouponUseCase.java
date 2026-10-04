package br.com.onebrain.couponapi.application.usecase;

import br.com.onebrain.couponapi.application.domain.CouponDomain;
import br.com.onebrain.couponapi.application.domain.exception.CouponNotFoundException;
import br.com.onebrain.couponapi.application.domain.service.CouponDomainService;
import br.com.onebrain.couponapi.application.port.CouponRepositoryPort;

/**
 * CASO DE USO: DeleteCouponUseCase
 * 
 * Responsabilidade: ORQUESTRAR a deleção de cupom.
 * 
 * Fluxo:
 * 1. Busca cupom via porta
 * 2. Delega marcação como deletado ao domínio
 * 3. Persiste via porta
 * 
 * O serviço de domínio valida o estado do cupom antes de deletar.
 */
public class DeleteCouponUseCase {
    private final CouponDomainService domainService;
    private final CouponRepositoryPort repository;

    public DeleteCouponUseCase(
            CouponDomainService domainService,
            CouponRepositoryPort repository) {
        this.domainService = domainService;
        this.repository = repository;
    }

    public void execute(String code) {
        // 1. Busca cupom
        CouponDomain coupon = repository.findByCode(code)
            .orElseThrow(() -> new CouponNotFoundException(code));

        // 2. Serviço de domínio marca como deletado (valida estado)
        //    Pode lançar: CouponAlreadyDeletedException
        domainService.deleteCoupon(coupon);

        // 3. Persistir mudança
        repository.save(coupon);
    }
}
