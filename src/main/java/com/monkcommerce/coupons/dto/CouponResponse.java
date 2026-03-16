package com.monkcommerce.coupons.dto;

import com.monkcommerce.coupons.enums.CouponType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CouponResponse {
    private Long id;
    private CouponType type;
    private String name;
    private String description;
    private boolean active;
    private LocalDateTime expirationDate;
    private String detailsJson;
}