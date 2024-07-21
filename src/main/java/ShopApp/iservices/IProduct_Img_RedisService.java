/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.iservices;

import ShopApp.responses.Product_ImgResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import org.springframework.data.domain.PageRequest;

/**
 *
 * @author mac
 */
public interface IProduct_Img_RedisService {
    void clear();
    
    List<Product_ImgResponse> getAllItems(
            String keyword, 
            PageRequest pageRequest,
            String name) throws JsonProcessingException;
    
    void saveAllItems(
            List<Product_ImgResponse> item,
            String keyword,
            PageRequest pageRequest,
            String name)throws JsonProcessingException;
}
