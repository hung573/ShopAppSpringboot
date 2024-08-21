/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;
import ShopApp.components.LocalizationUtils;
import ShopApp.dtos.OrderDTO;
import ShopApp.services.Order.IOrderService;
import ShopApp.models.Order;
import ShopApp.models.User;
import ShopApp.responses.ListResponse;
import ShopApp.responses.MessageResponse;
import ShopApp.responses.ObjectResponse;
import ShopApp.responses.OrderResponse;
import ShopApp.services.Order.OrderService;
import ShopApp.utils.MessageKey;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        
    @GetMapping("/get-order-by-keyword")
    private ResponseEntity<?> getAllOrder(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit){
        try {
            // Điều chỉnh page để bắt đầu từ 1 thay vì 0
            int adjustedPage = page > 0 ? page - 1 : 0;
            // Tạo Pageable từ page và limit
            PageRequest pageRequest = PageRequest.of(adjustedPage, limit,
                    Sort.by("id").ascending());

            Page<OrderResponse> orderPage = orderService.getOrderByKeyWord(keyword,pageRequest);
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
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/add")
    private ResponseEntity<ObjectResponse> addOerder(@Valid @RequestBody OrderDTO ortDTO, BindingResult result){
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
            Order order = orderService.creteOrder(ortDTO);
            return ResponseEntity.ok(ObjectResponse.builder()
                    .items(OrderResponse.fromOrder(order))
                    .message(localizationUtils.getLocalizedMessage(MessageKey.ADD_SUCCESSFULLY))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ObjectResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, e.getMessage()))
                        .build());
        }
    }
    
    //Lấy ra danh sách order từ user_id
    @GetMapping("/user_order/{user_id}")
    private ResponseEntity<?> getUserIdMyOrder(@PathVariable("user_id") long idUser){
        try {
            //kiem tra user dang nhap
            User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (loginUser.getId() != idUser) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            List<Order> listOrders = orderService.getAllByUserId(idUser);
            List<OrderResponse> listOrderResponses = listOrders
                    .stream()
                    .map(OrderResponse :: fromOrder)
                    .toList();
            return ResponseEntity.ok(listOrderResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Lấy ra chi tiết 1 order từ danh sách
    @GetMapping("/order/{id}")
    private ResponseEntity<ObjectResponse> getOrderId(@PathVariable("id") long id){
        try {
            Order order = orderService.getOrderById(id);
            return ResponseEntity.ok(ObjectResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.GETID_SUCCESSFULLY))
                    .items(OrderResponse.fromOrder(order))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ObjectResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, e.getMessage()))
                    .build());
        }
    }
    
    @PutMapping("/update/{id}")
    private ResponseEntity<?> updateOrder(@PathVariable("id")long id ,@Valid @RequestBody OrderDTO orderDTO, BindingResult result){
        try {
            Order order = orderService.updateOrder(id, orderDTO);
            return ResponseEntity.ok(ObjectResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.UPDATE_SUCCESSFULLY))
                    .items(OrderResponse.fromOrder(order))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ObjectResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, e.getMessage()))
                    .build());
        }
    }
    
    // thuc hien xoa mem => chuyen acti == false
    @DeleteMapping("/delete/{id}")
    private ResponseEntity<MessageResponse> deleteOrder(@PathVariable("id")long id){
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok(MessageResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.DELETE1_SUCCESSFULLY, id))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MessageResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, e.getMessage()))
                    .build());
        }
    }

}
