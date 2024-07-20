/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.responses;

import ShopApp.models.Product;
import ShopApp.models.ProductImage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.JoinColumn;
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
public class Product_ImgResponse {
    private Long id;
    
    @JsonProperty("image_url")
    private String imageURL;
    
    @JsonProperty("product_id")
    private Long productId;
    
    @JsonProperty("product_name")
    private String productName;
    
    public static Product_ImgResponse fromProductIMG(ProductImage productImage){
        return Product_ImgResponse.builder()
                .id(productImage.getId())
                .imageURL(productImage.getImageURL())
                .productId(productImage.getProduct().getId())
                .productName(productImage.getProduct().getName())
                .build();
    }
}
