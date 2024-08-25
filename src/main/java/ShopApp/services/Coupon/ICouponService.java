/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.services.Coupon;

import ShopApp.models.Coupon;
import ShopApp.models.CouponCondition;
import ShopApp.responses.CouponConditionReponse;
import java.util.List;

/**
 *
 * @author mac
 */
public interface ICouponService {
    
    Coupon getCouponCode(String couponCode) throws Exception;
    
    float calculateCouponValue(String couponCode, float totalAmount) throws Exception;
    
    List<CouponConditionReponse> getAllCoupon() throws Exception;
}
