/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.services.Product;

import ShopApp.responses.ProductResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import org.springframework.data.domain.PageRequest;

/**
 *
 * @author mac
 */
public interface IProductRedisService {
    
    void clear();
    
    List<ProductResponse> getAllProducts(
            String keyword, 
            Long categoryId,
            PageRequest pageRequest) throws JsonProcessingException;
    
    void saveAllProducts(
            List<ProductResponse> productResponses,
            String keyword,
            Long categoryId,
            PageRequest pageRequest)throws JsonProcessingException;
}
