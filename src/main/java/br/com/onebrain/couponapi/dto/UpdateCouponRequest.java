package br.com.onebrain.couponapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class UpdateCouponRequest {

    @NotBlank
    private String description;

    @NotNull
    @DecimalMin(value = "0.5", inclusive = true)
    private BigDecimal discountValue;

    @NotNull
    private LocalDate expirationDate;

    private boolean published = false;

}

