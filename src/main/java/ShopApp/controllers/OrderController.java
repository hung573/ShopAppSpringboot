/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;
import ShopApp.dtos.OrderDTO;
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
@RequestMapping("${api.prefix}/orders")
public class OrderController {
    
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
            return ResponseEntity.ok("Create Order Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/{user_id}")
    private ResponseEntity<?> getIdCategory(@PathVariable("user_id") long idUser){
        try {
            return ResponseEntity.ok("Get Successfully idUser My Order: "+ idUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/update/{id}")
    private ResponseEntity<?> updateOrder(@PathVariable("id")long id ,@Valid @RequestBody OrderDTO orderDTO, BindingResult result){
        try {
            return ResponseEntity.ok("Update Order Successfully id: "+ id + orderDTO.getEmail());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // thuc hien xoa mem => chuyen acti == false
    @DeleteMapping("/delete/{id}")
    private ResponseEntity<?> deleteOrder(@PathVariable("id")long id){
        try {
            return ResponseEntity.ok("Delete Order Successfully id: "+ id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
