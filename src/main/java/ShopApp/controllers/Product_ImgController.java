/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;

import ShopApp.components.LocalizationUtils;
import ShopApp.responses.ListResponse;
import ShopApp.responses.Product_ImgResponse;
import ShopApp.services.Product_ImgRedisService;
import ShopApp.services.Product_ImgService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author mac
 */

@RestController
@RequestMapping("${api.prefix}/product-images")
@RequiredArgsConstructor
public class Product_ImgController {
    private final LocalizationUtils localizationUtils;
    private final Product_ImgService product_ImgService;
    private final Product_ImgRedisService product_ImgRedisService;
    
    @GetMapping("")
    private ResponseEntity<ListResponse> getAllProduct_IMG( 
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) throws JsonProcessingException, Exception{
        
        String name = "all_product_imgs";
        // Điều chỉnh page để bắt đầu từ 1 thay vì 0
        int adjustedPage = page > 0 ? page - 1 : 0;

        // Tạo Pageable từ adjustedPage và limit
        PageRequest pageRequest = PageRequest.of(adjustedPage, limit,
                Sort.by("id").ascending()
        );
        int totalPages = 0;
        List<Product_ImgResponse> product_imgs = product_ImgRedisService.getAllItems(keyword, pageRequest, name);
        if (product_imgs == null) {
            Page<Product_ImgResponse> product_IMGPage = product_ImgService.getAllProductIMGSearch(keyword, pageRequest);
            // tong trang
            totalPages = product_IMGPage.getTotalPages();
            product_imgs = product_IMGPage.getContent();
            product_ImgRedisService.saveAllItems(product_imgs, keyword, pageRequest, name);
        }
        else if (keyword == null || !keyword.isEmpty()) {
            Page<Product_ImgResponse> product_IMGPage = product_ImgService.getAllProductIMGSearch(keyword, pageRequest);
            // tong trang
            totalPages = product_IMGPage.getTotalPages();
            product_imgs = product_IMGPage.getContent();
        }
        else{
            totalPages = product_ImgService.totalPages(limit);
        }
        
         // Create response
        ListResponse<Product_ImgResponse> ProductIMGListResponse = ListResponse
                .<Product_ImgResponse>builder()
                .items(product_imgs)
                .page(page)
                .totalPages(totalPages)
                .build();

        return ResponseEntity.ok(ProductIMGListResponse);
    }

}
