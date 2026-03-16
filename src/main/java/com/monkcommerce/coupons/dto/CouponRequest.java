package com.monkcommerce.coupons.dto;

import com.monkcommerce.coupons.enums.CouponType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponRequest {
    @NotNull
    private CouponType type;

    @NotBlank
    private String name;

    private String description;

    private boolean active = true;

    private LocalDateTime expirationDate;

    /**
     * raw JSON string
     */
    @NotBlank
    private String detailsJson;
}