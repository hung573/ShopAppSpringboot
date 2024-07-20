/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.repositories;

import ShopApp.models.Product;
import ShopApp.models.ProductImage;
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
public interface ProductImageRepository extends JpaRepository<ProductImage, Long>{
     List<ProductImage> findByProductId(Long productId);
     
    @Query("SELECT p FROM ProductImage p WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR p.product.name LIKE %:keyword%)")
    Page<ProductImage> searchProductIMG
            (@Param("keyword") String keyword, Pageable pageable);
}
