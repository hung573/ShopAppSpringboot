/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;

import ShopApp.dtos.UserDTO;
import ShopApp.dtos.UserLoginDTO;
import ShopApp.dtos.AdminUpdateUserDTO;
import ShopApp.iservices.IUserService;
import ShopApp.models.User;
import ShopApp.responses.ListResponse;
import ShopApp.responses.LoginResponse;
import ShopApp.services.UserService;
import ShopApp.components.LocalizationUtils;
import ShopApp.dtos.RefreshTokenDTO;
import ShopApp.dtos.UserUpdateDTO;
import ShopApp.models.Token;
import ShopApp.responses.MessageResponse;
import ShopApp.responses.ObjectResponse;
import ShopApp.responses.UserResponse;
import ShopApp.services.TokenService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;



/**
 *
 * @author mac
 */

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final LocalizationUtils localizationUtils;
    private final TokenService tokenService;
    
    @GetMapping("")
    private ResponseEntity<ListResponse> getAllUsers(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit){
        
        // Điều chỉnh page để bắt đầu từ 1 thay vì 0
        int adjustedPage = page > 0 ? page - 1 : 0;
        // Tạo Pageable từ page và limit
        PageRequest pageRequest = PageRequest.of(adjustedPage, limit,
                Sort.by("id").ascending());
        
        Page<UserResponse> userPage = userService.searchUsers(keyword,pageRequest);
        int totalPages = userPage.getTotalPages();
        List<UserResponse> users = userPage.getContent();
         // Create response
        ListResponse<UserResponse> orderDetailListResponse = ListResponse
                .<UserResponse>builder()
                .items(users)
                .page(page)
                .totalPages(totalPages)
                .build();
        return ResponseEntity.ok(orderDetailListResponse);
       
          
    }
    
    @PostMapping("/resigter")
    private ResponseEntity<MessageResponse> resigter(@Valid @RequestBody UserDTO userDTO, BindingResult result){
        try {
            if (result.hasErrors()) {
                List<String> errormessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(MessageResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR,errormessage.get(0)))
                        .build());
            }
            if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body(
                        MessageResponse.builder()
                                .message(localizationUtils.getLocalizedMessage(MessageKey.PASSWORD_NOT_MATCH))
                                .build()
                );
            }
            User user = userService.createUser(userDTO);
            return ResponseEntity.ok(MessageResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.RIGISTER_SUCCESSFULLY))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MessageResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.RIGISTER_FAILED, e.getMessage()))
                    .build());
        }
                                    
    }
    @PostMapping("/login")
    private ResponseEntity<LoginResponse> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO,
            BindingResult result,
            HttpServletRequest request){
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
            String userAgent = request.getHeader("User-Agent");
            User user = userService.getUserDetailFromToken(token);
            Token jwtToken = tokenService.addToken(user, token, isMobileDevice(userAgent));
            return ResponseEntity.ok(LoginResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.LOGIN_SUCCESSFULLY))
                    .token(jwtToken.getToken())
                    .tokenType(jwtToken.getTokenType())
                    .refreshToken(jwtToken.getRefreshToken())
                    .username(user.getUsername())
                    .roles(user.getAuthorities().stream().map(item -> item.getAuthority()).toList())
                    .id(user.getId())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(LoginResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.LOGIN_FAILED, e.getMessage()))
                    .token("")
                    .build());
        }
    }
    private boolean isMobileDevice(String userAgent) {
        // Kiểm tra User-Agent header để xác định thiết bị di động
        // Ví dụ đơn giản:
        return userAgent.toLowerCase().contains("mobile");
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
    
    @PutMapping("/admin/update/{id}")
    private ResponseEntity<ObjectResponse> UpdateUserAdmin(@PathVariable("id") long id,
            @Valid @RequestBody AdminUpdateUserDTO userUpdateDTO,
            BindingResult result){
        try {
            if (result.hasErrors()) {
                List<String> errormessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(ObjectResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, errormessage.get(0)))
                        .build());
            }
            User newUser = userService.updateUserAdmin(id, userUpdateDTO);
            return ResponseEntity.ok(ObjectResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.UPDATE_SUCCESSFULLY))
                    .items(UserResponse.fromUser(newUser))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ObjectResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR,e.getMessage()))
                            .build());
        }
    }
    
    @PutMapping("/details/{id}")
    private ResponseEntity<?> UpdateUser(
            @PathVariable("id") long id,
            @RequestBody UserUpdateDTO userUpdateDTO,
            @RequestHeader("Authorization") String authorizationHeader){
        try {
            String token = authorizationHeader.substring(7);
            User user = userService.getUserDetailFromToken(token);
            if (user.getId() != id) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            if (!userUpdateDTO.getPassword().equals(userUpdateDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body(ObjectResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKey.PASSWORD_NOT_MATCH))
                        .build());
            }
            User newUser = userService.updateUser(id, userUpdateDTO);
            return ResponseEntity.ok(ObjectResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.UPDATE_SUCCESSFULLY))
                    .items(UserResponse.fromUser(newUser))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ObjectResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR,e.getMessage()))
                            .build());
        }
    }
    
    @PostMapping("/details")
    private ResponseEntity<ObjectResponse> getDetailUser(@RequestHeader("Authorization") String authorizationHeader){
        try {
            String token = authorizationHeader.substring(7);
            User user = userService.getUserDetailFromToken(token);
            return ResponseEntity.ok(ObjectResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.GETID_SUCCESSFULLY))
                    .items(UserResponse.fromUser(user))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ObjectResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR,e.getMessage()))
                            .build());
        }
    }
    
    @GetMapping("/admin/{id}")
    private ResponseEntity<ObjectResponse> getDetailUserFormId(
            @PathVariable("id") long idUser){
        try {
            User user = new User();
            user = userService.getUserDetailFromId(idUser);
            return ResponseEntity.ok(ObjectResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.GETID_SUCCESSFULLY))
                    .items(UserResponse.fromUser(user))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ObjectResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR,e.getMessage()))
                            .build());
        }
    }
    
    @PostMapping("/check")
    private ResponseEntity<?> checkToken(@RequestHeader("Authorization") String authorizationHeader){
        String token = authorizationHeader.substring(7);
        if (tokenService.checktoken(token)) {
            return ResponseEntity.ok(MessageResponse.builder()
                    .message(token)
                    .build());
        }
        return ResponseEntity.badRequest().body(MessageResponse.builder()
                    .message(null)
                    .build());
    }
    
    @PostMapping("/refreshToken")
    public ResponseEntity<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenDTO refreshTokenDTO)
    {
        try {
            User userDetail = userService.getUserDetailsFromRefreshToken(refreshTokenDTO.getRefreshToken());
            Token jwtToken = tokenService.refreshToken(refreshTokenDTO.getRefreshToken(), userDetail);
            return ResponseEntity.ok(LoginResponse.builder()
                    .message("Refresh token successfully")
                    .token(jwtToken.getToken())
                    .tokenType(jwtToken.getTokenType())
                    .refreshToken(jwtToken.getRefreshToken())
                    .username(userDetail.getUsername())
                    .roles(userDetail.getAuthorities().stream().map(item -> item.getAuthority()).toList())
                    .id(userDetail.getId())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKey.LOGIN_FAILED, e.getMessage()))
                            .build()
            );
        }
    }
    
    
}
