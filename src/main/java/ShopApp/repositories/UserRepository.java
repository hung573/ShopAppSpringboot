/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.repositories;

import ShopApp.models.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mac
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    
    boolean existsByPhoneNumber(String phoneNumBer); // kiem tra sdt co ton tai khong
    
    Optional<User> findByPhoneNumber(String phoneNumBer); // tim kiem user theo sdt
    
}
