package com.monkcommerce.coupons.service;

import com.monkcommerce.coupons.dto.CartRequest;
import com.monkcommerce.coupons.dto.CartResponse;
import com.monkcommerce.coupons.entity.Coupon;
import com.monkcommerce.coupons.enums.CouponType;

public interface CouponStrategy {
    CouponType getType();
    boolean isApplicable(Coupon coupon, CartRequest cartRequest);
    double calculateDiscount(Coupon coupon, CartRequest cartRequest);
    CartResponse applyCoupon(Coupon coupon, CartRequest cartRequest);
}