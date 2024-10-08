/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.responses;

import ShopApp.models.Category;
import ShopApp.models.Product;
import ShopApp.models.ProductImage;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mac
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse extends BaseResponse{
    
    private Long id;
    
    private String name;
    
    private Float price;
    
    private String description;
    
    private String thumbnail;
    
    private int totalPage;
    
    @JsonProperty("product_images")
    private List<ProductImage> productImages = new ArrayList<>();
    
    @JsonProperty("category_id")
    private Long categoryId;
    
    private Boolean active;//thuộc về admin
    
    public static ProductResponse fromProduct(Product product){
        ProductResponse productResponse = ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .description(product.getDescription())
                    .thumbnail(product.getThumbnail())
                    .productImages(product.getProductImages())
                    .categoryId(product.getCategory().getId())
                    .active(product.getActive())
                    .build();
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setUpdatedAt(product.getUpdatedAt());
        return productResponse;
    }
}
