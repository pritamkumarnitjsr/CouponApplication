package com.monkcommerce.coupons.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemRequest {
    @NotNull
    private Long productId;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotNull
    @DecimalMin("0.0")
    private Double price;
}