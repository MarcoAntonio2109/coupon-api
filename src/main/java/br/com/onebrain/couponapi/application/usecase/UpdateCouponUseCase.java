package br.com.onebrain.couponapi.application.usecase;

import br.com.onebrain.couponapi.application.domain.CouponDomain;
import br.com.onebrain.couponapi.application.domain.exception.CouponNotFoundException;
import br.com.onebrain.couponapi.application.domain.service.CouponDomainService;
import br.com.onebrain.couponapi.application.port.CouponRepositoryPort;

import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * CASO DE USO: UpdateCouponUseCase
 * 
 * Responsabilidade: ORQUESTRAR a atualização de cupom.
 * 
 * Fluxo:
 * 1. Busca cupom existente via porta
 * 2. Delega validação e atualização ao CouponDomainService
 * 3. Persiste via porta
 */
public class UpdateCouponUseCase {
    private final CouponDomainService domainService;
    private final CouponRepositoryPort repository;

    public UpdateCouponUseCase(
            CouponDomainService domainService,
            CouponRepositoryPort repository) {
        this.domainService = domainService;
        this.repository = repository;
    }

    public CouponDomain execute(
            String code,
            String description,
            BigDecimal discountValue,
            LocalDate expirationDate,
            boolean published) {

        // 1. Busca cupom existente
        CouponDomain coupon = repository.findByCode(code)
            .orElseThrow(() -> new CouponNotFoundException(code));

        // 2. Serviço de domínio valida e atualiza (REGRAS DE NEGÓCIO)
        //    Pode lançar: InvalidExpirationDateException
        domainService.updateCoupon(
            coupon,
            description,
            discountValue,
            expirationDate,
            published
        );

        // 3. Persiste mudanças
        return repository.save(coupon);
    }
}
