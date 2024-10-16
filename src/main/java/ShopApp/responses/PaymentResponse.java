/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.responses;

import ShopApp.models.Payment;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PaymentResponse {
    @JsonProperty("payment_name")
    private String paymentName;
    
    public static PaymentResponse fromPayment(Payment payment){
        return PaymentResponse.builder()
                .paymentName(payment.getPaymentName())
                .build();
    }
}
