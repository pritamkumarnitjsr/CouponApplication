package com.monkcommerce.coupons.service.strategy;

import com.monkcommerce.coupons.dto.*;
import com.monkcommerce.coupons.entity.Coupon;
import com.monkcommerce.coupons.enums.CouponType;
import com.monkcommerce.coupons.exception.BadRequestException;
import com.monkcommerce.coupons.service.CouponStrategy;
import com.monkcommerce.coupons.util.JsonUtil;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class BxGyCouponStrategy implements CouponStrategy {

    @Override
    public CouponType getType() {
        return CouponType.BXGY;
    }

    @Override
    public boolean isApplicable(Coupon coupon, CartRequest cartRequest) {
        return calculateDiscount(coupon, cartRequest) > 0;
    }

    @Override
    public double calculateDiscount(Coupon coupon, CartRequest cartRequest) {
        Map<String, Object> details = JsonUtil.toMap(coupon.getDetailsJson());

        List<Map<String, Object>> buyProducts = getList(details, "buyProducts");
        List<Map<String, Object>> getProducts = getList(details, "getProducts");
        int repetitionLimit = getInt(details, "repetitionLimit");

        Map<Long, Integer> cartQtyMap = buildQuantityMap(cartRequest);

        int timesApplicableByBuy = Integer.MAX_VALUE;

        for (Map<String, Object> buyProduct : buyProducts) {
            long productId = ((Number) buyProduct.get("productId")).longValue();
            int requiredQty = ((Number) buyProduct.get("quantity")).intValue();
            int cartQty = cartQtyMap.getOrDefault(productId, 0);

            timesApplicableByBuy = Math.min(timesApplicableByBuy, cartQty / requiredQty);
        }

        if (timesApplicableByBuy == Integer.MAX_VALUE) {
            timesApplicableByBuy = 0;
        }

        int maxRepetition = Math.min(timesApplicableByBuy, repetitionLimit);
        if (maxRepetition <= 0) {
            return 0;
        }

        Map<Long, Double> priceMap = buildPriceMap(cartRequest);
        double totalDiscount = 0;

        for (Map<String, Object> getProduct : getProducts) {
            long productId = ((Number) getProduct.get("productId")).longValue();
            int freeQtyPerRep = ((Number) getProduct.get("quantity")).intValue();
            int cartQty = cartQtyMap.getOrDefault(productId, 0);

            int totalEligibleFreeQty = Math.min(cartQty, freeQtyPerRep * maxRepetition);
            totalDiscount += totalEligibleFreeQty * priceMap.getOrDefault(productId, 0.0);
        }

        return round(totalDiscount);
    }

    @Override
    public CartResponse applyCoupon(Coupon coupon, CartRequest cartRequest) {
        Map<String, Object> details = JsonUtil.toMap(coupon.getDetailsJson());

        List<Map<String, Object>> buyProducts = getList(details, "buyProducts");
        List<Map<String, Object>> getProducts = getList(details, "getProducts");
        int repetitionLimit = getInt(details, "repetitionLimit");

        Map<Long, Integer> cartQtyMap = buildQuantityMap(cartRequest);
        Map<Long, Double> priceMap = buildPriceMap(cartRequest);

        int timesApplicableByBuy = Integer.MAX_VALUE;
        for (Map<String, Object> buyProduct : buyProducts) {
            long productId = ((Number) buyProduct.get("productId")).longValue();
            int requiredQty = ((Number) buyProduct.get("quantity")).intValue();
            int cartQty = cartQtyMap.getOrDefault(productId, 0);
            timesApplicableByBuy = Math.min(timesApplicableByBuy, cartQty / requiredQty);
        }

        if (timesApplicableByBuy == Integer.MAX_VALUE) {
            timesApplicableByBuy = 0;
        }

        int applicableRepetition = Math.min(timesApplicableByBuy, repetitionLimit);

        Map<Long, Double> discountPerProduct = new HashMap<>();
        for (Map<String, Object> getProduct : getProducts) {
            long productId = ((Number) getProduct.get("productId")).longValue();
            int freeQtyPerRep = ((Number) getProduct.get("quantity")).intValue();

            int cartQty = cartQtyMap.getOrDefault(productId, 0);
            int freeQty = Math.min(cartQty, freeQtyPerRep * applicableRepetition);
            double itemDiscount = freeQty * priceMap.getOrDefault(productId, 0.0);

            discountPerProduct.put(productId, round(itemDiscount));
        }

        List<DiscountBreakdown> items = cartRequest.getItems().stream()
                .map(item -> DiscountBreakdown.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .totalDiscount(discountPerProduct.getOrDefault(item.getProductId(), 0.0))
                        .build())
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

    private Map<Long, Integer> buildQuantityMap(CartRequest cartRequest) {
        Map<Long, Integer> map = new HashMap<>();
        for (CartItemRequest item : cartRequest.getItems()) {
            map.put(item.getProductId(), map.getOrDefault(item.getProductId(), 0) + item.getQuantity());
        }
        return map;
    }

    private Map<Long, Double> buildPriceMap(CartRequest cartRequest) {
        Map<Long, Double> map = new HashMap<>();
        for (CartItemRequest item : cartRequest.getItems()) {
            map.put(item.getProductId(), item.getPrice());
        }
        return map;
    }

    private List<Map<String, Object>> getList(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null || !(value instanceof List<?>)) {
            throw new BadRequestException("Missing or invalid field in detailsJson: " + key);
        }
        return (List<Map<String, Object>>) value;
    }

    private int getInt(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            throw new BadRequestException("Missing field in detailsJson: " + key);
        }
        return ((Number) value).intValue();
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}