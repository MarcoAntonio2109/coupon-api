package br.com.onebrain.couponapi.application.port;

import br.com.onebrain.couponapi.application.domain.CouponDomain;

import java.util.List;
import java.util.Optional;

/**
 * Porta de saída (Driven Port) - Adaptador de Persistência
 * Interface que define operações de armazenamento de cupons.
 * A aplicação não conhece como os dados são persistidos (banco, cache, arquivo, etc).
 */
public interface CouponRepositoryPort {
    CouponDomain save(CouponDomain coupon);
    Optional<CouponDomain> findByCode(String code);
    boolean existsByCode(String code);
    List<CouponDomain> findAllByDeletedFalse(int page, int size);
    Long countByDeletedFalse();
}
