/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.repositories;

import ShopApp.models.Coupon;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author mac
 */
public interface CouponRepository extends JpaRepository<Coupon, Long>{
     Optional<Coupon> findByCode(String couponCode);
}
