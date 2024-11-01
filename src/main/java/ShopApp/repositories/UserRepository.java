/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.repositories;

import ShopApp.models.User;
import java.util.Optional;
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
public interface UserRepository extends JpaRepository<User, Long>{
    
    boolean existsByPhoneNumber(String phoneNumBer); // kiem tra sdt co ton tai khong
    
    Optional<User> findByPhoneNumber(String phoneNumBer); // tim kiem user theo sdt
    
    @Query("SELECT u FROM User u WHERE "+
            "(:keyword IS NULL OR :keyword = '' "+
            "OR u.fullName LIKE %:keyword% "+
            "OR u.phoneNumber LIKE %:keyword% "+
            "OR u.address LIKE %:keyword%) "+
            "AND LOWER(u.role.name) = 'user'")
    Page<User> searchUser(
            @Param("keyword") String keyword,
            Pageable pageable);
    
    
    
}
