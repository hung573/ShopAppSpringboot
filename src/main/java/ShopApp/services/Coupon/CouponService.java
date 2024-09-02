/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.services.Coupon;

import ShopApp.components.LocalizationUtils;
import ShopApp.exception.DataNotFoudException;
import ShopApp.models.Coupon;
import ShopApp.models.CouponCondition;
import ShopApp.repositories.CouponConditionRepository;
import ShopApp.utils.MessageKey;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ShopApp.repositories.CouponRepository;
import ShopApp.responses.CouponConditionReponse;
import java.util.stream.Collectors;

/**
 *
 * @author mac
 */
@Service
@RequiredArgsConstructor
public class CouponService implements ICouponService{
    
    private final CouponRepository couponRepository;
    private final CouponConditionRepository couponConditionRepository; 
    private final LocalizationUtils localizationUtils;
    
    @Override
    public Coupon getCouponCode(String couponCode) throws Exception{
        return couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));
    }
    
    @Override
    public float calculateCouponValue(String couponCode, float totalAmount) throws Exception {
        Coupon coupon = getCouponCode(couponCode);
        
        if (!coupon.isActive()) {
            throw new IllegalArgumentException("Coupon is not active");
        }
        
        float discount = calculateDiscount(coupon, totalAmount);
        float finalAmount = totalAmount - discount;
        return finalAmount;
    }
    
    private float calculateDiscount(Coupon coupon, float totalAmount){
        List<CouponCondition> conditions = couponConditionRepository
                .findByCouponId(coupon.getId());
        float discount = 0;
        float updatedTotalAmount = totalAmount;
        
        for (CouponCondition condition : conditions) {
            //EAV(Entity - Attribute - Value) Model
            String attribute = condition.getAttribute();
            String operator = condition.getOperator();
            String value = condition.getValue();

            float percentDiscount = Float.valueOf(
                    String.valueOf(condition.getDiscount_amount()));

            if (attribute.equals("minimum_amount")) {
                if (operator.equals(">") && updatedTotalAmount > Double.parseDouble(value)) {
                    discount += updatedTotalAmount * percentDiscount / 100;
                }
            } else if (attribute.equals("applicable_date")) {
                LocalDate applicableDate = LocalDate.parse(value);
                LocalDate currentDate = LocalDate.now();
                if (operator.equalsIgnoreCase("BETWEEN")
                        && currentDate.isEqual(applicableDate)) {
                    discount += updatedTotalAmount * percentDiscount / 100;
                }
            }
            //còn nhiều nhiều điều kiện khác nữa
            updatedTotalAmount = updatedTotalAmount - discount;
        }
        return discount;
    }

    @Override
    public List<CouponConditionReponse> getAllCouponConditions() throws Exception{
        List<CouponCondition> conditions = couponConditionRepository.findAll();
        return conditions.stream()
                .map(CouponConditionReponse::fromCouponConditionReponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Coupon> getAllCoupon() throws Exception{
        return couponRepository.findAll();
    }

    @Override
    public Coupon getConponId(Long id) throws Exception {
        return couponRepository.findById(id)
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));
    }


    
}
