/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package ShopApp.responses;

import ShopApp.models.Order;
import ShopApp.models.OrderDetail;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.JoinColumn;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
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
public class OrderResponse {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("fullname")
    private String fullName;

    private String email;
    
    @JsonProperty("phone_number")
    private String phoneNumber;

    private String address;

    private String note;
    
    @JsonProperty("order_date")
    private Date orderDate;

    private String status;

    @JsonProperty("total_money")
    private Float totalMoney;
    
    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("shipping_address")
    private String shippingAddress;
    
    @JsonProperty("shipping_date")
    private Date shippingDate;
    
    @JsonProperty("tracking_number")
    private String trackingNumber;
    
    @JsonProperty("payment_method")
    private String paymentMethod;
    
    @JsonProperty("order_details")
    @JsonManagedReference
    private List<OrderDetailResponse> orderDetails;
    
    public static OrderResponse fromOrder(Order order){
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .fullName(order.getFullName())
                .email(order.getEmail())
                .phoneNumber(order.getPhoneNumber())
                .address(order.getAddress())
                .note(order.getNote())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalMoney(order.getTotalMoney())
                .shippingMethod(order.getShippingMethod())
                .shippingAddress(order.getShippingAddress())
                .shippingDate(order.getShippingDate())
                .trackingNumber(order.getTrackingNumber())
                .paymentMethod(order.getPaymentMethod())
                .orderDetails(
                        order.getOrderDetails().stream()
                                .map(OrderDetailResponse::fromOrderDetail)
                                .collect(Collectors.toList())
                )
                .build();
    }

}
