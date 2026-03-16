package com.monkcommerce.coupons.entity;

import com.monkcommerce.coupons.enums.CouponType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType type;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    private boolean active;

    private LocalDateTime expirationDate;

    /**
     * We are storing coupon details as JSON string for flexibility.
     * Example:
     * CART_WISE -> {"threshold":1000,"discountPercentage":10}
     * PRODUCT_WISE -> {"productId":1,"discountPercentage":20}
     * BXGY -> {
     *   "buyProducts":[{"productId":1,"quantity":2},{"productId":2,"quantity":1}],
     *   "getProducts":[{"productId":3,"quantity":1}],
     *   "repetitionLimit":2
     * }
     */
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String detailsJson;
}