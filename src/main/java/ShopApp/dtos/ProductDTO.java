/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author mac
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    @NotEmpty(message = "Tên sản phẩm không được để tróng")
    @Size(min = 3, max = 300, message = "Tên sản phẩm không được dưới 3 ký tự và trên 300 ký tự")
    private String name;
    
    @Min(value = 1, message = "Giá sản phẩm không được nhỏ hơn 0")
    private Float price;
    
    @Size(min = 3, max = 300, message = "Tên sản phẩm không được dưới 3 ký tự và trên 300 ký tự")
    private String description;
    
    private String thumbnail;
    
    @JsonProperty("category_id")
    private Long categoryId;
    
//    private List<MultipartFile> files;  
}
