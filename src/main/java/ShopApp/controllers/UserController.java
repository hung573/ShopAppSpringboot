/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;

import ShopApp.dtos.UserDTO;
import ShopApp.dtos.UserLoginDTO;
import ShopApp.iservices.IUserService;
import ShopApp.models.User;
import ShopApp.responses.ListResponse;
import ShopApp.services.UserService;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



/**
 *
 * @author mac
 */
@Controller
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("")
    private ResponseEntity<ListResponse> getAllUsers(@RequestParam("page") int page, @RequestParam("limit") int limit){
        PageRequest pageRequest = PageRequest.of(page, limit,
                Sort.by("id").descending());
        
        Page<User> userPage = userService.getAllUser(pageRequest);
        int totalPages = userPage.getTotalPages();
        List<User> users = userPage.getContent();
         // Create response
        ListResponse<User> orderDetailListResponse = ListResponse
                .<User>builder()
                .items(users)
                .page(page)
                .totalPages(totalPages)
                .build();

        return ResponseEntity.ok(orderDetailListResponse);
       
          
    }
    
    @PostMapping("/resigter")
    private ResponseEntity<?> resigter(@Valid @RequestBody UserDTO userDTO, BindingResult result){
        try {
            if (result.hasErrors()) {
                List<String> errormessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errormessage);
            }
            if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body("Password nhập lại chưa chính xác!!!");
            }
            userService.createUser(userDTO);
            return ResponseEntity.ok("Đăng ký thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/login")
    private ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO, BindingResult result){
        try {
            if (result.hasErrors()) {
                List<String> errormessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errormessage);
            }
            String token = userService.login(userLoginDTO.getPhoneNumber(), userLoginDTO.getPassword());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/delete/{id}")
    private ResponseEntity<String> DeleteUser(@PathVariable("id") long id){
        try {
            
            userService.deleteUser(id);
            return ResponseEntity.ok("Đã xoá thành công userId: "+ id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
