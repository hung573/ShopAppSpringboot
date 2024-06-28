/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.repositories;

import ShopApp.models.Product;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mac
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
    
    boolean existsByName(String name); // kiem tra ten san pham co ton tai khong
    
    Page<Product> findAll(Pageable pageable); // phan trang
    
}
