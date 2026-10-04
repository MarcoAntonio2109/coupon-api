package br.com.onebrain.couponapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
public class CouponResponse {

    private Long id;
    private String code;
    private String description;
    private BigDecimal discountValue;
    private LocalDate expirationDate;
    private boolean published;
    private boolean deleted;
    private OffsetDateTime createdAt;

}

