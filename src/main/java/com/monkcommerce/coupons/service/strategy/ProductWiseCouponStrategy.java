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
public class ProductWiseCouponStrategy implements CouponStrategy {

    @Override
    public CouponType getType() {
        return CouponType.PRODUCT_WISE;
    }

    @Override
    public boolean isApplicable(Coupon coupon, CartRequest cartRequest) {
        Map<String, Object> details = JsonUtil.toMap(coupon.getDetailsJson());
        long productId = getLong(details, "productId");
        return cartRequest.getItems().stream().anyMatch(i -> i.getProductId().equals(productId));
    }

    @Override
    public double calculateDiscount(Coupon coupon, CartRequest cartRequest) {
        if (!isApplicable(coupon, cartRequest)) {
            return 0;
        }

        Map<String, Object> details = JsonUtil.toMap(coupon.getDetailsJson());
        long productId = getLong(details, "productId");
        double discountPercentage = getDouble(details, "discountPercentage");

        return round(cartRequest.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .mapToDouble(i -> i.getPrice() * i.getQuantity() * discountPercentage / 100.0)
                .sum());
    }

    @Override
    public CartResponse applyCoupon(Coupon coupon, CartRequest cartRequest) {
        Map<String, Object> details = JsonUtil.toMap(coupon.getDetailsJson());
        long productId = getLong(details, "productId");
        double discountPercentage = getDouble(details, "discountPercentage");

        List<DiscountBreakdown> items = cartRequest.getItems().stream()
                .map(item -> {
                    double discount = 0;
                    if (item.getProductId().equals(productId)) {
                        discount = item.getPrice() * item.getQuantity() * discountPercentage / 100.0;
                    }
                    return DiscountBreakdown.builder()
                            .productId(item.getProductId())
                            .quantity(item.getQuantity())
                            .price(item.getPrice())
                            .totalDiscount(round(discount))
                            .build();
                })
                .collect(Collectors.toList());

        double totalPrice = cartRequest.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        double totalDiscount = items.stream()
                .mapToDouble(DiscountBreakdown::getTotalDiscount)
                .sum();

        return CartResponse.builder()
                .items(items)
                .totalPrice(round(totalPrice))
                .totalDiscount(round(totalDiscount))
                .finalPrice(round(totalPrice - totalDiscount))
                .build();
    }

    private long getLong(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            throw new BadRequestException("Missing field in detailsJson: " + key);
        }
        return ((Number) value).longValue();
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