/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mac
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {
    @JsonProperty("order_id")
    private Long orderId;
    
    @JsonProperty("product_id")
    private Long productId;
    
    @Min(value = 0, message = "Giá không được nhỏ hơn 0")
    private Float price;
    
    @JsonProperty("number_of_products")
    @Min(value = 1, message = "Số lượng ít nhất phải có 1")
    private int numberOfProduct;
    
    @JsonProperty("total_money")
    @Min(value = 0, message = "Tổng giá tiền không được nhỏ hơn 0")
    private Float totalMoney;
    
    @NotBlank(message = "Màu sản phẩm không được để tróng")
    private String color;
}
