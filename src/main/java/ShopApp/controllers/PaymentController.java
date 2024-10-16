/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;

import ShopApp.components.LocalizationUtils;
import ShopApp.models.Payment;
import ShopApp.responses.ListResponse;
import ShopApp.responses.ObjectResponse;
import ShopApp.responses.PaymentResponse;
import ShopApp.services.Payment.PaymentService;
import ShopApp.utils.MessageKey;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author mac
 */
@RestController
@RequestMapping("${api.prefix}/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final LocalizationUtils localizationUtils;
    
    @GetMapping("")
    private ResponseEntity<ListResponse> getAllPayment() throws Exception{
        List<Payment> listPayment = paymentService.getListPayment();
        ListResponse<Payment> listResponse = ListResponse.<Payment>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKey.GETID_SUCCESSFULLY))
                .items(listPayment)
                .page(0)
                .totalPages(0)
                .build();
        return ResponseEntity.ok(listResponse);
    }
    
    @GetMapping("/{id}")
    private ResponseEntity<ObjectResponse> getPaymentId(@PathVariable("id") long id) throws Exception{
        ObjectResponse<PaymentResponse> objectResponse = ObjectResponse.<PaymentResponse>builder()
                .items(PaymentResponse.fromPayment(paymentService.getPaymentById(id)))
                .message(localizationUtils.getLocalizedMessage(MessageKey.GETID_SUCCESSFULLY))
                .build();
        
        return ResponseEntity.ok(objectResponse);
    }
}
