/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;

import ShopApp.components.LocalizationUtils;
import ShopApp.dtos.OrderDetailDTO;
import ShopApp.services.OrderDetail.IOrderDetailService;
import ShopApp.models.OrderDetail;
import ShopApp.responses.ListResponse;
import ShopApp.responses.MessageResponse;
import ShopApp.responses.ObjectResponse;
import ShopApp.responses.OrderDetailResponse;
import ShopApp.services.OrderDetail.OrderDetailService;
import ShopApp.utils.MessageKey;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author mac
 */
@RestController
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor
public class OrderDetailController {

    private final OrderDetailService orderDetailService;
    private final LocalizationUtils localizationUtils;

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ListResponse> getAllOrderDetails(@RequestParam("page") int page, @RequestParam("limit") int limit) {
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
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> createOrderDetail(@Valid @RequestBody OrderDetailDTO orderDetailDTO, BindingResult result) throws Exception {
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
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getOrderDetailId(@Valid @PathVariable("id") long id) throws Exception {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail = orderDetailService.getOrderDetailById(id);
        return ResponseEntity.ok(ObjectResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKey.GETID_SUCCESSFULLY))
                .items(OrderDetailResponse.fromOrderDetail(orderDetail))
                .build());
//          return ResponseEntity.ok(orderDetailService.getOrderDetailById(id)); // hiển thị kiểu chi tiết
    }

    // Lay ds orderDetails tu id Order
    @GetMapping("/order/{order_id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> ListOrderDetails(@Valid @PathVariable("order_id") long orderId) {
        List<OrderDetail> listOrderDetails = orderDetailService.getAllByOrderId(orderId);
        List<OrderDetailResponse> orderResponses = listOrderDetails
                .stream()
                .map(OrderDetailResponse::fromOrderDetail)
                .toList();
        return ResponseEntity.ok(orderResponses);
//            return ResponseEntity.ok(orderResponses); // hiển thị kiểu chi tiết
    }

    // Update OrderDetail
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> updateOrderDetail(
            @PathVariable("id") long id,
            @Valid @RequestBody OrderDetailDTO orderDetailDTO,
            BindingResult result) throws Exception {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail = orderDetailService.updateOrderDetail(id, orderDetailDTO);
        return ResponseEntity.ok(ObjectResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKey.UPDATE_SUCCESSFULLY))
                .items(OrderDetailResponse.fromOrderDetail(orderDetail))
                .build());
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> deleteOrderDetail(@PathVariable("id") long id) throws Exception {
        orderDetailService.deleteOrderDetail(id);
        return ResponseEntity.ok(MessageResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKey.DELETE_SUCCESSFULLY))
                .build());
    }
}
