/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;

import ShopApp.components.LocalizationUtils;
import ShopApp.dtos.OrderDetailDTO;
import ShopApp.iservices.IOrderDetailService;
import ShopApp.models.OrderDetail;
import ShopApp.responses.ListResponse;
import ShopApp.responses.MessageResponse;
import ShopApp.responses.ObjectResponse;
import ShopApp.responses.OrderDetailResponse;
import ShopApp.services.OrderDetailService;
import ShopApp.utils.MessageKey;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final LocalizationUtils localizationUtils;

    
    @GetMapping("")
     private ResponseEntity<ListResponse> getAllOrderDetails(@RequestParam("page") int page, @RequestParam("limit") int limit){
        // Tạo Pageable từ page và limit
        PageRequest pageRequest = PageRequest.of(page, limit,
                Sort.by("id").ascending());
        
        Page<OrderDetailResponse> orderDetailsPage = orderDetailService.getAllOrderDetails(pageRequest);
        // tong trang
        int totalPages = orderDetailsPage.getTotalPages();
        
        List<OrderDetailResponse> orderDetails = orderDetailsPage.getContent();
        
        // Create response
        ListResponse<OrderDetailResponse> orderDetailListResponse = ListResponse.<OrderDetailResponse>builder()
                .items(orderDetails)
                .page(page)
                .totalPages(totalPages)
                .build();

        return ResponseEntity.ok(orderDetailListResponse);
    }
    
    @PostMapping("/add")
    private ResponseEntity<ObjectResponse> createOrderDetail(@Valid @RequestBody OrderDetailDTO orderDetailDTO, BindingResult result){
        try {
            if (result.hasErrors()) {
                List<String> errormessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(ObjectResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, errormessage))
                        .build());
            }
            OrderDetail orderDetail = new OrderDetail();
            orderDetail = orderDetailService.creteOrderDetail(orderDetailDTO);
            return ResponseEntity.ok(ObjectResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.ADD_SUCCESSFULLY))
                    .items(OrderDetailResponse.fromOrderDetail(orderDetail))
                    .build());
//            return ResponseEntity.ok(orderDetail); // hiển thị kiểu chi tiết

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ObjectResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, e.getMessage()))
                        .build());
        }
    }
    
    @GetMapping("/{id}")
    private ResponseEntity<ObjectResponse> getOrderDetailId(@Valid @PathVariable("id") long id){
        try {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail = orderDetailService.getOrderDetailById(id);
            return ResponseEntity.ok(ObjectResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.GETID_SUCCESSFULLY))
                    .items(OrderDetailResponse.fromOrderDetail(orderDetail))
                    .build());
//          return ResponseEntity.ok(orderDetailService.getOrderDetailById(id)); // hiển thị kiểu chi tiết
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ObjectResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, e.getMessage()))
                        .build());
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
    private ResponseEntity<ObjectResponse> updateOrderDetail(
            @PathVariable("id")long id ,
            @Valid @RequestBody OrderDetailDTO orderDetailDTO,
            BindingResult result){
        try {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail = orderDetailService.updateOrderDetail(id, orderDetailDTO);
            return ResponseEntity.ok(ObjectResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.UPDATE_SUCCESSFULLY))
                    .items(OrderDetailResponse.fromOrderDetail(orderDetail))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ObjectResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, e.getMessage()))
                        .build());
        }
    }
    @DeleteMapping("/delete/{id}")
    private ResponseEntity<MessageResponse> deleteOrderDetail(@PathVariable("id")long id){
        try {
            orderDetailService.deleteOrderDetail(id);
            return ResponseEntity.ok(MessageResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.DELETE_SUCCESSFULLY))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MessageResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR,e.getMessage()))
                    .build());
        }
    }
}
