package com.monkcommerce.coupons.controller;

import com.monkcommerce.coupons.dto.*;
import com.monkcommerce.coupons.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/coupons")
    @ResponseStatus(HttpStatus.CREATED)
    public CouponResponse createCoupon(@Valid @RequestBody CouponRequest request) {
        return couponService.createCoupon(request);
    }

    @GetMapping("/coupons")
    public List<CouponResponse> getAllCoupons() {
        return couponService.getAllCoupons();
    }

    @GetMapping("/coupons/{id}")
    public CouponResponse getCouponById(@PathVariable Long id) {
        return couponService.getCouponById(id);
    }

    @PutMapping("/coupons/{id}")
    public CouponResponse updateCoupon(@PathVariable Long id, @Valid @RequestBody CouponRequest request) {
        return couponService.updateCoupon(id, request);
    }

    @DeleteMapping("/coupons/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
    }

    @PostMapping("/applicable-coupons")
    public List<ApplicableCouponResponse> getApplicableCoupons(@Valid @RequestBody CartRequest cartRequest) {
        return couponService.getApplicableCoupons(cartRequest);
    }

    @PostMapping("/apply-coupon/{id}")
    public ApplyCouponResponse applyCoupon(@PathVariable Long id,
                                           @Valid @RequestBody CartRequest cartRequest) {
        return couponService.applyCoupon(id, cartRequest);
    }
}