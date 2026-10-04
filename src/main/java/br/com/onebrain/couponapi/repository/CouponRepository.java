package br.com.onebrain.couponapi.repository;

import br.com.onebrain.couponapi.model.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCode(String code);
    boolean existsByCode(String code);
    Page<Coupon> findAllByDeletedFalse(Pageable pageable);
}

