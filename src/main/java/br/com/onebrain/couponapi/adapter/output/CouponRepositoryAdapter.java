package br.com.onebrain.couponapi.adapter.output;

import br.com.onebrain.couponapi.application.domain.CouponDomain;
import br.com.onebrain.couponapi.application.port.CouponRepositoryPort;
import br.com.onebrain.couponapi.model.Coupon;
import br.com.onebrain.couponapi.repository.CouponRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de Persistência (Driven Adapter)
 * Implementa a porta CouponRepositoryPort usando Spring Data JPA.
 * Converte entre o domínio (CouponDomain) e a entidade JPA (Coupon).
 */
@Component
public class CouponRepositoryAdapter implements CouponRepositoryPort {
    
    private final CouponRepository jpaRepository;

    public CouponRepositoryAdapter(CouponRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CouponDomain save(CouponDomain coupon) {
        Coupon entity = toCoupon(coupon);
        Coupon saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<CouponDomain> findByCode(String code) {
        return jpaRepository.findByCode(code)
            .map(this::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }

    @Override
    public List<CouponDomain> findAllByDeletedFalse(int page, int size) {
        org.springframework.data.domain.Pageable pageable = 
            org.springframework.data.domain.PageRequest.of(page, size);
        return jpaRepository.findAllByDeletedFalse(pageable)
            .getContent()
            .stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Long countByDeletedFalse() {
        org.springframework.data.domain.Pageable pageable = 
            org.springframework.data.domain.PageRequest.of(0, 1);
        return jpaRepository.findAllByDeletedFalse(pageable)
            .getTotalElements();
    }

    private Coupon toCoupon(CouponDomain domain) {
        Coupon coupon = new Coupon();
        coupon.setId(domain.getId());
        coupon.setCode(domain.getCode());
        coupon.setDescription(domain.getDescription());
        coupon.setDiscountValue(domain.getDiscountValue());
        coupon.setExpirationDate(domain.getExpirationDate());
        coupon.setPublished(domain.isPublished());
        coupon.setDeleted(domain.isDeleted());
        coupon.setCreatedAt(domain.getCreatedAt());
        coupon.setDeletedAt(domain.getDeletedAt());
        return coupon;
    }

    private CouponDomain toDomain(Coupon entity) {
        CouponDomain domain = new CouponDomain();
        domain.setId(entity.getId());
        domain.setCode(entity.getCode());
        domain.setDescription(entity.getDescription());
        domain.setDiscountValue(entity.getDiscountValue());
        domain.setExpirationDate(entity.getExpirationDate());
        domain.setPublished(entity.isPublished());
        domain.setDeleted(entity.isDeleted());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setDeletedAt(entity.getDeletedAt());
        return domain;
    }
}
