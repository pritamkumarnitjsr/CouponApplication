package com.monkcommerce.coupons.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplicableCouponResponse {
    private Long couponId;
    private String type;
    private String name;
    private Double discount;
}