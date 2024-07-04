/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;

import ShopApp.dtos.UserDTO;
import ShopApp.dtos.UserLoginDTO;
import ShopApp.dtos.UserUpdateDTO;
import ShopApp.iservices.IUserService;
import ShopApp.models.User;
import ShopApp.responses.ListResponse;
import ShopApp.responses.LoginResponse;
import ShopApp.services.UserService;
import ShopApp.components.LocalizationUtils;
import ShopApp.responses.MessageResponse;
import ShopApp.responses.UserResponse;
import ShopApp.utils.MessageKey;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;



/**
 *
 * @author mac
 */
@Controller
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final LocalizationUtils localizationUtils;
    
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
    private ResponseEntity<UserResponse> resigter(@Valid @RequestBody UserDTO userDTO, BindingResult result){
        try {
            if (result.hasErrors()) {
                List<String> errormessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(UserResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR,errormessage.get(0)))
                        .build());
            }
            if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body(
                        UserResponse.builder()
                                .message(localizationUtils.getLocalizedMessage(MessageKey.PASSWORD_NOT_MATCH))
                                .build()
                );
            }
            User user = userService.createUser(userDTO);
            return ResponseEntity.ok(UserResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.RIGISTER_SUCCESSFULLY))
                    .user(user)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(UserResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.RIGISTER_FAILED, e.getMessage()))
                    .build());
        }
                                    
    }
    @PostMapping("/login")
    private ResponseEntity<LoginResponse> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO,
            BindingResult result){
        try {
            if (result.hasErrors()) {
                List<String> errormessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(
                        LoginResponse.builder()
                        .message(errormessage.get(0))
                        .token("")
                        .build());
            }
            String token = userService.login(userLoginDTO.getPhoneNumber(), userLoginDTO.getPassword());
            return ResponseEntity.ok(LoginResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.LOGIN_SUCCESSFULLY))
                    .token(token)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(LoginResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.LOGIN_FAILED, e.getMessage()))
                    .token("")
                    .build());
        }
    }
    
    @DeleteMapping("/delete/{id}")
    private ResponseEntity<MessageResponse> DeleteUser(@PathVariable("id") long id){
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(MessageResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.DELETE_SUCCESSFULLY))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, e.getMessage()))
                    .build());
        }
    }
    
    @PutMapping("/update/{id}")
    private ResponseEntity<UserResponse> UpdateUser(@PathVariable("id") long id,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO,
            BindingResult result){
        try {
            if (result.hasErrors()) {
                List<String> errormessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(UserResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, errormessage.get(0)))
                        .build());
            }
            if (!userUpdateDTO.getPassword().equals(userUpdateDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body(UserResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKey.PASSWORD_NOT_MATCH))
                        .build());
            }
            User newUser = userService.updateUser(id, userUpdateDTO);
            return ResponseEntity.ok(UserResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.UPDATE_SUCCESSFULLY))
                    .user(newUser)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    UserResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR,e.getMessage()))
                            .build());
        }
    }
}
