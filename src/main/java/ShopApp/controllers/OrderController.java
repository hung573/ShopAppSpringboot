/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;

import ShopApp.components.LocalizationUtils;
import ShopApp.components.converters.OrderMessageConverter;
import ShopApp.dtos.OrderDTO;
import ShopApp.dtos.OrderUpdateDTO;
import ShopApp.dtos.sendEmail.SendEmailDTO;
import ShopApp.dtos.sendEmail.ToDTO;
import ShopApp.services.Order.IOrderService;
import ShopApp.models.Order;
import ShopApp.models.User;
import ShopApp.responses.ListResponse;
import ShopApp.responses.MessageResponse;
import ShopApp.responses.ObjectResponse;
import ShopApp.responses.OrderResponse;
import ShopApp.services.Order.OrderService;
import ShopApp.services.SendEmail.SendEmailOrderService;
import ShopApp.utils.MessageKey;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

/**
 *
 * @author mac
 */
@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final LocalizationUtils localizationUtils;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @GetMapping("/get-order-by-keyword")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllOrder(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) throws Exception {
        // Điều chỉnh page để bắt đầu từ 1 thay vì 0
        int adjustedPage = page > 0 ? page - 1 : 0;
        // Tạo Pageable từ page và limit
        PageRequest pageRequest = PageRequest.of(adjustedPage, limit,
                Sort.by("id").ascending());

        Page<OrderResponse> orderPage = orderService.getOrderByKeyWord(keyword, pageRequest);
        // tong trang
        int totalPages = orderPage.getTotalPages();

        List<OrderResponse> orders = orderPage.getContent();

        // Create response
        ListResponse<OrderResponse> orderListResponse = ListResponse.<OrderResponse>builder()
                .items(orders)
                .page(page)
                .totalPages(totalPages)
                .build();
        return ResponseEntity.ok(orderListResponse);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> addOerder(@Valid @RequestBody OrderDTO ortDTO, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            List<String> errormessage = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ObjectResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, errormessage))
                    .build());
        }
        Order order = orderService.creteOrder(ortDTO);
        
        this.kafkaTemplate.setMessageConverter(new OrderMessageConverter());
        this.kafkaTemplate.send("send-email-order-confrim", OrderResponse.fromOrder(order));//producer
        
        return ResponseEntity.ok(ObjectResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKey.ADD_SUCCESSFULLY))
                .items(OrderResponse.fromOrder(order))
                .build());
    }

    //Lấy ra danh sách order từ user_id
    @GetMapping("/user_order/{user_id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getUserIdMyOrder(@PathVariable("user_id") long idUser) throws Exception {
        //kiem tra user dang nhap
        User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loginUser.getId() != idUser) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Order> listOrders = orderService.getAllByUserId(idUser);
        List<OrderResponse> listOrderResponses = listOrders
                .stream()
                .map(OrderResponse::fromOrder)
                .toList();
        return ResponseEntity.ok(listOrderResponses);
    }

    // Lấy ra chi tiết 1 order từ danh sách
    @GetMapping("/order/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getOrderId(@PathVariable("id") long id) throws Exception {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(ObjectResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKey.GETID_SUCCESSFULLY))
                .items(OrderResponse.fromOrder(order))
                .build());
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateOrder(@PathVariable("id") long id, @Valid @RequestBody OrderUpdateDTO orderDTO, BindingResult result) throws Exception {
        Order order = orderService.updateOrder(id, orderDTO);
        return ResponseEntity.ok(ObjectResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKey.UPDATE_SUCCESSFULLY))
                .items(OrderResponse.fromOrder(order))
                .build());
    }

    // thuc hien xoa mem => chuyen acti == false
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> deleteOrder(@PathVariable("id") long id) throws Exception {
        orderService.deleteOrder(id);
        return ResponseEntity.ok(MessageResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKey.DELETE1_SUCCESSFULLY, id))
                .build());
    }

}
