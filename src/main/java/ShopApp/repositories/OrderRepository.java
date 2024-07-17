/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.repositories;

import ShopApp.models.Order;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mac
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{
    List<Order> findByUserId(Long userId);
    
    @Query("SELECT o FROM Order o WHERE "+
            "(:keyword IS NULL OR :keyword = '' OR o.fullName LIKE %:keyword% OR o.phoneNumber LIKE %:keyword% OR o.address LIKE %:keyword% "+
            "OR o.note LIKE %:keyword%)")
    Page<Order> searchOrders(
            @Param("keyword") String keyword,
            Pageable pageable);
}
