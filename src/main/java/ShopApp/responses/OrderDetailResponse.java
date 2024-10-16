/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.responses;

import ShopApp.models.OrderDetail;
import ShopApp.models.Product;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class OrderDetailResponse {
    
    private Long id;
    
    @JsonProperty("order_id")
    private Long orderId;

//    @JsonProperty("product_id")
//    private Long productId;
    
    @JsonProperty("product")
    private ProductResponse product;

    private Float price;
    
    @JsonProperty("number_of_products")
    private int numberOfProducts;
    
    @JsonProperty("total_money")
    private Float totalMoney;

    private String color;
    
    public static OrderDetailResponse fromOrderDetail(OrderDetail orderDetail){
        return OrderDetailResponse.builder()
                .id(orderDetail.getId())
                .orderId(orderDetail.getOrder().getId())
                .product(ProductResponse.fromProduct(orderDetail.getProduct()))
                .price(orderDetail.getPrice())
                .numberOfProducts(orderDetail.getNumberOfProducts())
                .totalMoney(orderDetail.getTotalMoney())
                .color(orderDetail.getColor())
                .build();
    }
    
}
