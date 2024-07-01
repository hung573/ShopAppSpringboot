/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;
import ShopApp.dtos.OrderDTO;
import ShopApp.iservices.IOrderService;
import ShopApp.models.Order;
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
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final IOrderService orderService;
    
    
    @PostMapping("/add")
    private ResponseEntity<?> addOerder(@Valid @RequestBody OrderDTO ortDTO, BindingResult result){
        try {
            if (result.hasErrors()) {
                List<String> errormessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errormessage);
            }
            
            Order order = orderService.creteOrder(ortDTO);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    //Lấy ra danh sách order từ user_id
    @GetMapping("/user_order/{user_id}")
    private ResponseEntity<?> getUserIdMyOrder(@PathVariable("user_id") long idUser){
        try {
            List<Order> listOrders = orderService.getAllByUserId(idUser);
            return ResponseEntity.ok(listOrders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Lấy ra chi tiết 1 order từ danh sách
    @GetMapping("/{id}")
    private ResponseEntity<?> getOrderId(@PathVariable("id") long id){
        try {
            Order order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/update/{id}")
    private ResponseEntity<?> updateOrder(@PathVariable("id")long id ,@Valid @RequestBody OrderDTO orderDTO, BindingResult result){
        try {
            Order order = orderService.updateOrder(id, orderDTO);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // thuc hien xoa mem => chuyen acti == false
    @DeleteMapping("/delete/{id}")
    private ResponseEntity<?> deleteOrder(@PathVariable("id")long id){
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok("Delete Order Successfully id: "+ id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
