package com.monkcommerce.coupons.service;

import com.monkcommerce.coupons.dto.*;
import com.monkcommerce.coupons.entity.Coupon;
import com.monkcommerce.coupons.exception.BadRequestException;
import com.monkcommerce.coupons.exception.ResourceNotFoundException;
import com.monkcommerce.coupons.repository.CouponRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final Map<String, CouponStrategy> strategyMap = new HashMap<>();

    public CouponService(CouponRepository couponRepository, List<CouponStrategy> strategies) {
        this.couponRepository = couponRepository;
        for (CouponStrategy strategy : strategies) {
            strategyMap.put(strategy.getType().name(), strategy);
        }
    }

    public CouponResponse createCoupon(CouponRequest request) {
        validateCouponRequest(request);

        Coupon coupon = Coupon.builder()
                .type(request.getType())
                .name(request.getName())
                .description(request.getDescription())
                .active(request.isActive())
                .expirationDate(request.getExpirationDate())
                .detailsJson(request.getDetailsJson())
                .build();

        Coupon saved = couponRepository.save(coupon);
        return mapToResponse(saved);
    }

    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CouponResponse getCouponById(Long id) {
        return mapToResponse(getCouponEntity(id));
    }

    public CouponResponse updateCoupon(Long id, CouponRequest request) {
        validateCouponRequest(request);

        Coupon coupon = getCouponEntity(id);
        coupon.setType(request.getType());
        coupon.setName(request.getName());
        coupon.setDescription(request.getDescription());
        coupon.setActive(request.isActive());
        coupon.setExpirationDate(request.getExpirationDate());
        coupon.setDetailsJson(request.getDetailsJson());

        Coupon updated = couponRepository.save(coupon);
        return mapToResponse(updated);
    }

    public void deleteCoupon(Long id) {
        Coupon coupon = getCouponEntity(id);
        couponRepository.delete(coupon);
    }

    public List<ApplicableCouponResponse> getApplicableCoupons(CartRequest cartRequest) {
        return couponRepository.findAll().stream()
                .filter(this::isCouponUsableNow)
                .map(coupon -> {
                    CouponStrategy strategy = getStrategy(coupon);
                    if (!strategy.isApplicable(coupon, cartRequest)) {
                        return null;
                    }
                    double discount = strategy.calculateDiscount(coupon, cartRequest);
                    if (discount <= 0) {
                        return null;
                    }
                    return ApplicableCouponResponse.builder()
                            .couponId(coupon.getId())
                            .type(coupon.getType().name())
                            .name(coupon.getName())
                            .discount(discount)
                            .build();
                })
                .filter(Objects::nonNull)
                .sorted((a, b) -> Double.compare(b.getDiscount(), a.getDiscount()))
                .collect(Collectors.toList());
    }

    public ApplyCouponResponse applyCoupon(Long couponId, CartRequest cartRequest) {
        Coupon coupon = getCouponEntity(couponId);

        if (!isCouponUsableNow(coupon)) {
            throw new BadRequestException("Coupon is inactive or expired");
        }

        CouponStrategy strategy = getStrategy(coupon);

        if (!strategy.isApplicable(coupon, cartRequest)) {
            throw new BadRequestException("Coupon conditions not met");
        }

        CartResponse cartResponse = strategy.applyCoupon(coupon, cartRequest);

        return ApplyCouponResponse.builder()
                .couponId(coupon.getId())
                .couponType(coupon.getType().name())
                .couponName(coupon.getName())
                .updatedCart(cartResponse)
                .build();
    }

    private Coupon getCouponEntity(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
    }

    private CouponStrategy getStrategy(Coupon coupon) {
        CouponStrategy strategy = strategyMap.get(coupon.getType().name());
        if (strategy == null) {
            throw new BadRequestException("No strategy found for coupon type: " + coupon.getType());
        }
        return strategy;
    }

    private boolean isCouponUsableNow(Coupon coupon) {
        return coupon.isActive() &&
                (coupon.getExpirationDate() == null || coupon.getExpirationDate().isAfter(LocalDateTime.now()));
    }

    private void validateCouponRequest(CouponRequest request) {
        if (request.getType() == null) {
            throw new BadRequestException("Coupon type is required");
        }
        if (request.getDetailsJson() == null || request.getDetailsJson().isBlank()) {
            throw new BadRequestException("detailsJson is required");
        }
    }

    private CouponResponse mapToResponse(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .type(coupon.getType())
                .name(coupon.getName())
                .description(coupon.getDescription())
                .active(coupon.isActive())
                .expirationDate(coupon.getExpirationDate())
                .detailsJson(coupon.getDetailsJson())
                .build();
    }
}