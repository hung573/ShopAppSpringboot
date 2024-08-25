/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.responses;

import ShopApp.models.Coupon;
import ShopApp.models.CouponCondition;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mac
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponConditionReponse {
    private long id;
    
    private String couponCode;
    
    private String attribute;
    
    private String operator;
    
    private String value;
    
    private float discount_amount;
    
    public static CouponConditionReponse fromCouponConditionReponse(CouponCondition condition){
        return CouponConditionReponse.builder()
                .id(condition.getId())
                .couponCode(condition.getCoupon().getCode())
                .attribute(condition.getAttribute())
                .operator(condition.getOperator())
                .value(condition.getValue())
                .discount_amount(condition.getDiscount_amount())
                .build();
    }
   
}
