package com.monkcommerce.coupons.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplyCouponResponse {
    private Long couponId;
    private String couponType;
    private String couponName;
    private CartResponse updatedCart;
}