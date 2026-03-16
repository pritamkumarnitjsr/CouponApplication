package com.monkcommerce.coupons.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CartRequest {
    @Valid
    @NotEmpty
    private List<CartItemRequest> items;
}