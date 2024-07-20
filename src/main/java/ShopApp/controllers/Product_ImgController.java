/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;

import ShopApp.components.LocalizationUtils;
import ShopApp.responses.ListResponse;
import ShopApp.responses.Product_ImgResponse;
import ShopApp.services.Product_ImgService;
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
    
    @GetMapping("")
    private ResponseEntity<ListResponse> getAllProduct_IMG( 
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit){
        
        // Điều chỉnh page để bắt đầu từ 1 thay vì 0
        int adjustedPage = page > 0 ? page - 1 : 0;

        // Tạo Pageable từ adjustedPage và limit
        PageRequest pageRequest = PageRequest.of(adjustedPage, limit,
                Sort.by("id").ascending()
        );
        
        Page<Product_ImgResponse> product_IMGPage = product_ImgService.getAllProductIMGSearch(keyword, pageRequest);
        // tong trang
        int totalPages = product_IMGPage.getTotalPages();
        List<Product_ImgResponse> product_imgs = product_IMGPage.getContent();
        
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
