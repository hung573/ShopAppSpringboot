/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;

import ShopApp.dtos.OrderDetailDTO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author mac
 */
@Controller
@RequestMapping("${api.prefix}/order_details")
public class OrderDetailController {
    
    @PostMapping("/add")
    private ResponseEntity<?> createOrderDetail(@Valid @RequestBody OrderDetailDTO orderDetailDTO, BindingResult result){
        try {
            if (result.hasErrors()) {
                List<String> errormessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errormessage);
            }
            return ResponseEntity.ok("Create OrderDetails Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("{id}")
    private ResponseEntity<?> getOrderDetailId(@Valid @PathVariable("id") long id){
        try {
            return ResponseEntity.ok("getOrderDetails with for id: "+ id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Lay ds orderDetails tu id Order
    @GetMapping("/order/{order_id}")
    private ResponseEntity<?> ListOrderDetail(@Valid @PathVariable("order_id") long  orderId){
        return ResponseEntity.ok("getOrderDetails with orderId: "+ orderId);
    }
    
    // Update OrderDetail
    @PutMapping("/update/{id}")
    private ResponseEntity<?> updateOrderDetail(
            @PathVariable("id")long id ,
            @Valid @RequestBody OrderDetailDTO orderDetailDTO,
            BindingResult result){
        try {
            return ResponseEntity.ok("Update OrderDetail Successfully id: "+ id + ", "+orderDetailDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/delete/{id}")
    private ResponseEntity<?> deleteOrderDetail(@PathVariable("id")long id){
        return ResponseEntity.ok("Delete OrderDetail Successfully id: "+ id);
    }
}
