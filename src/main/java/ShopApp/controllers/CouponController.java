/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;

import ShopApp.components.LocalizationUtils;
import ShopApp.responses.CouponConditionReponse;
import ShopApp.responses.MessageResponse;
import ShopApp.responses.ObjectResponse;
import ShopApp.services.Coupon.CouponService;
import ShopApp.utils.MessageKey;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private ResponseEntity<?> calculateCouponValue(
            @RequestParam("couponCode") String couponCode,
            @RequestParam("totalAmount") float totalAmount){
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
    
    @GetMapping("/listCoupons")
    private ResponseEntity<?> getAllCoupons(){
        try {
            List<CouponConditionReponse> AllCouponConditionReponses = couponService.getAllCoupon();
            return ResponseEntity.ok(ObjectResponse.builder()
                    .items(AllCouponConditionReponses)
                    .message("")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MessageResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, e.getMessage()))
                        .build());
        }
    }
    
}
