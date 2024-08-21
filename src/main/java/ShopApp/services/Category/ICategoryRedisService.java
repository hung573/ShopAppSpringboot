/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.services.Category;

import ShopApp.responses.ObjectResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import org.springframework.data.domain.PageRequest;

/**
 *
 * @author mac
 */
public interface ICategoryRedisService<T> {
    void clear();
    
    List<T> getAllItems(
            String keyword, 
            PageRequest pageRequest,
            String name) throws JsonProcessingException;
    
    void saveAllItems(
            List<T> item,
            String keyword,
            PageRequest pageRequest,
            String name)throws JsonProcessingException;
}
