package com.monkcommerce.coupons.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CartResponse {
    private List<DiscountBreakdown> items;
    private Double totalPrice;
    private Double totalDiscount;
    private Double finalPrice;
}