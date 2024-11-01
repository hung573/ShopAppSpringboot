/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateDTO {
    @JsonProperty("user_id")
    private Long userId;
    
    @JsonProperty("fullname")
    @NotBlank(message = "Họ và tên không được để tróng")
    private String fullName;
    
    private String email;
    
    @JsonProperty("phone_number")
    @NotBlank(message = "Số điện thoại không được để tróng")
    private String phoneNumber;
    
    @NotBlank(message = "Địa chỉ không được để tróng")
    private String address;
    
    private String note;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("total_money")
    @Min(value = 0, message = "Tổng giá tiền không được nhỏ hơn 0")
    private Float totalMoney;
    
    @JsonProperty("shipping_method")
    private String shippingMethod;
    
    @JsonProperty("shipping_date")
    private Date shippingDate;
    
    @JsonProperty("shipping_address")
    private String shippingAddress;
    
    @JsonProperty("payment_method")
    @NotBlank(message = "Phương thức thanh toán không được để tróng")
    private String paymentMethod;
    
    @JsonProperty("cart_items")
    private List<CartItemDTO> cartItems;
}