/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.services.ProductImage;

import ShopApp.services.ProductImage.IProduct_Img_RedisService;
import ShopApp.responses.Product_ImgResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author mac
 */

@Service
@RequiredArgsConstructor
public class Product_ImgRedisService implements IProduct_Img_RedisService{
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper;
    
    @Value("${spring.data.redis.use-redis-cache}")
    private boolean useRedisCache;
    
    private String getKeyFrom(String keyword, PageRequest pageRequest, String name){
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize();
        Sort sort = pageRequest.getSort();
        String sortDirection = sort.getOrderFor("id")
                .getDirection() == Sort.Direction.ASC ? "asc" : "desc";
        String key = String.format("%s:%d:%d:%s", name,pageNumber, pageSize, sortDirection);
        return key;
        /*
         * {
         * "all_products:1:10:asc": "list of products object"
         * }
         */
    }
    
    @Override
    public void clear() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();

    }

    @Override
    public List<Product_ImgResponse> getAllItems(String keyword, PageRequest pageRequest, String name) throws JsonProcessingException {
        if(useRedisCache == false) {
            return null;
        }
        String key = this.getKeyFrom(keyword, pageRequest, name);
        String json = (String) redisTemplate.opsForValue().get(key);
        List<Product_ImgResponse> itemResponses = json != null
                ? redisObjectMapper.readValue(json, new TypeReference<List<Product_ImgResponse>>() {
                })
                : null;
        return itemResponses;
    }

    @Override
    public void saveAllItems(List<Product_ImgResponse> item, String keyword, PageRequest pageRequest, String name) throws JsonProcessingException {
        String key = this.getKeyFrom(keyword, pageRequest, name);
        String json = redisObjectMapper.writeValueAsString(item);
        redisTemplate.opsForValue().set(key, json);
    }
    
}
