/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;

import ShopApp.dtos.OrderDetailDTO;
import ShopApp.iservices.IOrderDetailService;
import ShopApp.models.OrderDetail;
import ShopApp.responses.OrderDetailResponse;
import ShopApp.services.OrderDetailService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class OrderDetailController {
    private final OrderDetailService orderDetailService;
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
            OrderDetail orderDetail = new OrderDetail();
            orderDetail = orderDetailService.creteOrderDetail(orderDetailDTO);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
//            return ResponseEntity.ok(orderDetail); // hiển thị kiểu chi tiết

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    private ResponseEntity<?> getOrderDetailId(@Valid @PathVariable("id") long id){
        try {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail = orderDetailService.getOrderDetailById(id);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
//          return ResponseEntity.ok(orderDetailService.getOrderDetailById(id)); // hiển thị kiểu chi tiết
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Lay ds orderDetails tu id Order
    @GetMapping("/order/{order_id}")
    private ResponseEntity<?> ListOrderDetails(@Valid @PathVariable("order_id") long  orderId){
        try {
            List<OrderDetail> listOrderDetails = orderDetailService.getAllByOrderId(orderId);
            List<OrderDetailResponse> orderResponses = listOrderDetails
                    .stream()
                    .map(OrderDetailResponse::fromOrderDetail)
                    .toList();
            return ResponseEntity.ok(orderResponses);
//            return ResponseEntity.ok(orderResponses); // hiển thị kiểu chi tiết

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Update OrderDetail
    @PutMapping("/update/{id}")
    private ResponseEntity<?> updateOrderDetail(
            @PathVariable("id")long id ,
            @Valid @RequestBody OrderDetailDTO orderDetailDTO,
            BindingResult result){
        try {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail = orderDetailService.updateOrderDetail(id, orderDetailDTO);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/delete/{id}")
    private ResponseEntity<?> deleteOrderDetail(@PathVariable("id")long id){
        try {
            orderDetailService.deleteOrderDetail(id);
            return ResponseEntity.ok("Delete OrderDetail Successfully id: "+ id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
