/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.services;

import ShopApp.models.Category;
import ShopApp.responses.ObjectResponse;
import ShopApp.responses.ProductResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ShopApp.iservices.ICategoryRedisService;

/**
 *
 * @author mac
 */
@Service
@RequiredArgsConstructor
public class CategoryRedisService implements ICategoryRedisService<Category>{
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper;
    
    
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
    public List<Category> getAllItems(String keyword, PageRequest pageRequest, String name) throws JsonProcessingException {
        String key = this.getKeyFrom(keyword, pageRequest, name);
        String json = (String) redisTemplate.opsForValue().get(key);
        List<Category> itemResponses = json != null
                ? redisObjectMapper.readValue(json, new TypeReference<List<Category>>() {
                })
                : null;
        return itemResponses;
    }

    @Override
    public void saveAllItems(List<Category> item, String keyword, PageRequest pageRequest, String name) throws JsonProcessingException {
        String key = this.getKeyFrom(keyword, pageRequest, name);
        String json = redisObjectMapper.writeValueAsString(item);
        redisTemplate.opsForValue().set(key, json);
    }
}
