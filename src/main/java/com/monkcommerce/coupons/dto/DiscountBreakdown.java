package com.monkcommerce.coupons.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiscountBreakdown {
    private Long productId;
    private Integer quantity;
    private Double price;
    private Double totalDiscount;
}