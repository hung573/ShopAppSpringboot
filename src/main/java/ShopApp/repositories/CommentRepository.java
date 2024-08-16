/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.repositories;

import ShopApp.models.Comment;
import ShopApp.responses.CommentResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author mac
 */
public interface CommentRepository extends JpaRepository<Comment, Long>{
    
    Page<Comment> findByProductId(long productId, Pageable pageable);
    
    Page<Comment> findByUserId(long userId, Pageable pageable);
    
    Page<Comment> findByUserIdAndProductId(long userId, long productId, Pageable pageable);
    
    @Query("SELECT c FROM Comment c WHERE " +
            "(:productId IS NULL OR :productId = 0 OR c.product.id = :productId) "+
            "AND (:keyword IS NULL OR :keyword = '' OR c.content LIKE %:keyword%)")
    Page<Comment> searchComments
            (@Param("productId") Long productId,
            @Param("keyword") String keyword, 
            Pageable pageable);

}
