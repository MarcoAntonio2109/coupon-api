package br.com.onebrain.couponapi.application.usecase;

import br.com.onebrain.couponapi.application.domain.CouponDomain;
import br.com.onebrain.couponapi.application.domain.exception.CouponAlreadyExistsException;
import br.com.onebrain.couponapi.application.domain.service.CouponDomainService;
import br.com.onebrain.couponapi.application.port.CouponRepositoryPort;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * CASO DE USO: CreateCouponUseCase
 * 
 * Responsabilidade: ORQUESTRAR o fluxo de criação de cupom.
 * 
 * ✓ Apenas coordena: chamando serviço de domínio e porta
 * ✓ Não contém lógica de negócio (está em CouponDomainService)
 * ✓ Agnóstico de tecnologia (sem Spring, sem JPA)
 * 
 * Fluxo:
 * 1. Recebe entrada (DTO convertido em tipos primitivos)
 * 2. Delega lógica de criação/validação a CouponDomainService
 * 3. Verifica integridade (existência via porta)
 * 4. Persiste via porta
 * 5. Retorna entidade de domínio
 */
public class CreateCouponUseCase {
    private final CouponDomainService domainService;
    private final CouponRepositoryPort repository;

    public CreateCouponUseCase(
            CouponDomainService domainService,
            CouponRepositoryPort repository) {
        this.domainService = domainService;
        this.repository = repository;
    }

    /**
     * Executa o caso de uso de criação.
     * 
     * Orquestração:
     * - CouponDomainService: Cria e valida (REGRAS DE NEGÓCIO)
     * - CouponRepositoryPort: Persiste (via porta, agnóstico)
     */
    public CouponDomain execute(
            String code,
            String description,
            BigDecimal discountValue,
            LocalDate expirationDate,
            boolean published) {

        // 1. Serviço de domínio cria e valida (Toda lógica de negócio aqui)
        //    Pode lançar: InvalidCouponCodeException, InvalidExpirationDateException
        CouponDomain coupon = domainService.createNewCoupon(
            code,
            description,
            discountValue,
            expirationDate,
            published
        );

        // 2. Verifica se código já existe (via porta)
        if (repository.existsByCode(coupon.getCode())) {
            throw new CouponAlreadyExistsException(coupon.getCode());
        }

        // 3. Persiste e retorna (via porta - agnóstico)
        return repository.save(coupon);
    }
}
