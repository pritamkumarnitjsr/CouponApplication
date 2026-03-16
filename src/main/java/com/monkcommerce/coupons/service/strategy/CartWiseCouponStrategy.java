package com.monkcommerce.coupons.service.strategy;

import com.monkcommerce.coupons.dto.*;
import com.monkcommerce.coupons.entity.Coupon;
import com.monkcommerce.coupons.enums.CouponType;
import com.monkcommerce.coupons.exception.BadRequestException;
import com.monkcommerce.coupons.service.CouponStrategy;
import com.monkcommerce.coupons.util.JsonUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CartWiseCouponStrategy implements CouponStrategy {

    @Override
    public CouponType getType() {
        return CouponType.CART_WISE;
    }

    @Override
    public boolean isApplicable(Coupon coupon, CartRequest cartRequest) {
        Map<String, Object> details = JsonUtil.toMap(coupon.getDetailsJson());
        double threshold = getDouble(details, "threshold");
        return getCartTotal(cartRequest) > threshold;
    }

    @Override
    public double calculateDiscount(Coupon coupon, CartRequest cartRequest) {
        if (!isApplicable(coupon, cartRequest)) {
            return 0;
        }
        Map<String, Object> details = JsonUtil.toMap(coupon.getDetailsJson());
        double discountPercentage = getDouble(details, "discountPercentage");
        return round(getCartTotal(cartRequest) * discountPercentage / 100.0);
    }

    @Override
    public CartResponse applyCoupon(Coupon coupon, CartRequest cartRequest) {
        double totalPrice = getCartTotal(cartRequest);
        double totalDiscount = calculateDiscount(coupon, cartRequest);

        List<DiscountBreakdown> items = cartRequest.getItems().stream()
                .map(item -> DiscountBreakdown.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .totalDiscount(0.0)
                        .build())
                .collect(Collectors.toList());

        return CartResponse.builder()
                .items(items)
                .totalPrice(round(totalPrice))
                .totalDiscount(round(totalDiscount))
                .finalPrice(round(totalPrice - totalDiscount))
                .build();
    }

    private double getCartTotal(CartRequest cartRequest) {
        return cartRequest.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
    }

    private double getDouble(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            throw new BadRequestException("Missing field in detailsJson: " + key);
        }
        return ((Number) value).doubleValue();
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}