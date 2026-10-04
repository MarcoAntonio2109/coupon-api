package br.com.onebrain.couponapi.application.usecase;

import br.com.onebrain.couponapi.application.domain.CouponDomain;
import br.com.onebrain.couponapi.application.domain.exception.CouponNotFoundException;
import br.com.onebrain.couponapi.application.port.CouponRepositoryPort;

/**
 * CASO DE USO: FindCouponUseCase
 * 
 * Responsabilidade: ORQUESTRAR a busca de cupom por código.
 * 
 * Fluxo simples:
 * 1. Recebe código
 * 2. Busca via porta
 * 3. Retorna ou lança exceção de domínio
 */
public class FindCouponUseCase {
    private final CouponRepositoryPort repository;

    public FindCouponUseCase(CouponRepositoryPort repository) {
        this.repository = repository;
    }

    public CouponDomain execute(String code) {
        return repository.findByCode(code)
            .orElseThrow(() -> new CouponNotFoundException(code));
    }
}
