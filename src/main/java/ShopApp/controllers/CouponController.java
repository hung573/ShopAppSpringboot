/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;

import ShopApp.components.LocalizationUtils;
import ShopApp.models.Coupon;
import ShopApp.responses.CouponConditionReponse;
import ShopApp.responses.MessageResponse;
import ShopApp.responses.ObjectResponse;
import ShopApp.services.Coupon.CouponService;
import ShopApp.utils.MessageKey;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author mac
 */
@RestController
@RequestMapping("${api.prefix}/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final LocalizationUtils localizationUtils;

    @GetMapping("")
    public ResponseEntity<ObjectResponse> calculateCouponValue(
            @RequestParam("couponCode") String couponCode,
            @RequestParam("totalAmount") float totalAmount) {
        try {
            float finalAmount = couponService.calculateCouponValue(couponCode, totalAmount);
            return ResponseEntity.ok(ObjectResponse.builder()
                    .items(finalAmount)
                    .message("")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ObjectResponse.builder()
                    .items(totalAmount)
                    .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, e.getMessage()))
                    .build());
        }
    }

    @GetMapping("/listCouponConditions")
    public ResponseEntity<?> getAllCouponConditions() throws Exception {
        List<CouponConditionReponse> AllCouponConditionReponses = couponService.getAllCouponConditions();
        return ResponseEntity.ok(ObjectResponse.builder()
                .items(AllCouponConditionReponses)
                .message("")
                .build());
    }
    
    @GetMapping("/listCoupons")
    public ResponseEntity<?> getAllCoupons() throws Exception {
        List<Coupon> AllCoupon = couponService.getAllCoupon();
        return ResponseEntity.ok(ObjectResponse.builder()
                .items(AllCoupon)
                .message("")
                .build());
    }
    
    @GetMapping("/getCouponId")
    public ResponseEntity<?> getCouponId( @RequestParam("couponId") Long id)throws Exception{
        return ResponseEntity.ok(ObjectResponse.builder()
                .items(couponService.getConponId(id))
                .message("")
                .build());
    }

}
