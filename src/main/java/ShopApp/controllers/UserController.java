/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;

import ShopApp.dtos.UserDTO;
import ShopApp.dtos.UserLoginDTO;
import ShopApp.dtos.AdminUpdateUserDTO;
import ShopApp.services.User.IUserService;
import ShopApp.models.User;
import ShopApp.responses.ListResponse;
import ShopApp.responses.LoginResponse;
import ShopApp.services.User.UserService;
import ShopApp.components.LocalizationUtils;
import ShopApp.dtos.RefreshTokenDTO;
import ShopApp.dtos.UserUpdateDTO;
import ShopApp.models.Token;
import ShopApp.responses.MessageResponse;
import ShopApp.responses.ObjectResponse;
import ShopApp.responses.UserResponse;
import ShopApp.services.Token.TokenService;
import ShopApp.utils.MessageKey;
import ShopApp.utils.ValidationUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ListResponse> getAllUsers(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit) {

        // Điều chỉnh page để bắt đầu từ 1 thay vì 0
        int adjustedPage = page > 0 ? page - 1 : 0;
        // Tạo Pageable từ page và limit
        PageRequest pageRequest = PageRequest.of(adjustedPage, limit,
                Sort.by("id").ascending());

        Page<UserResponse> userPage = userService.searchUsers(keyword, pageRequest);
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
    public ResponseEntity<MessageResponse> resigter(@Valid @RequestBody UserDTO userDTO, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            List<String> errormessage = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(MessageResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, errormessage.get(0)))
                    .build());
        }
        
        if (userDTO.getEmail() == null || userDTO.getEmail().trim().isBlank()) {
            if (userDTO.getPhoneNumber() == null || userDTO.getPhoneNumber().isBlank()) {
                return ResponseEntity.badRequest().body(MessageResponse.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("At least email or phone number is required")
                        .build());
            } else {
                //phone number not blank
                if (!ValidationUtils.isValidPhoneNumber(userDTO.getPhoneNumber())) {
                    throw new Exception("Invalid phone number");
                }
            }
        } else {
            //Email not blank
            if (!ValidationUtils.isValidEmail(userDTO.getEmail())) {
                throw new Exception("Invalid email format");
            }
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
                .status(HttpStatus.CREATED)
                .build());

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO,
            BindingResult result,
            HttpServletRequest request) {
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
            String token = userService.login(userLoginDTO);
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
                    //                    .message(localizationUtils.getLocalizedMessage(MessageKey.LOGIN_FAILED, e.getMessage()))
                    .message(e.getMessage())
                    .token("")
                    .build());
        }
    }

    public boolean isMobileDevice(String userAgent) {
        // Kiểm tra User-Agent header để xác định thiết bị di động
        // Ví dụ đơn giản:
        return userAgent.toLowerCase().contains("mobile");
    }

    @DeleteMapping("/delete/{id}/{acitve}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> DeleteUser(
            @PathVariable("id") long id,
            @PathVariable("acitve") int active) throws Exception {
        userService.blockOrEnableUser(id, active > 0);
        String message = active > 0 ? "Successfully enabled the user." : localizationUtils.getLocalizedMessage(MessageKey.DELETE_SUCCESSFULLY);
        return ResponseEntity.ok(MessageResponse.builder()
                .message(message)
                .build());
    }

    @PutMapping("/admin/update/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> UpdateUserAdmin(@PathVariable("id") long id,
            @Valid @RequestBody AdminUpdateUserDTO userUpdateDTO,
            BindingResult result) throws Exception {
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
    }

    @PutMapping("/details/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> UpdateUser(
            @PathVariable("id") long id,
            @RequestBody UserUpdateDTO userUpdateDTO,
            @RequestHeader("Authorization") String authorizationHeader) throws Exception {
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
    }

    @PostMapping("/details")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getDetailUser(@RequestHeader("Authorization") String authorizationHeader) throws Exception {
        String token = authorizationHeader.substring(7);
        User user = userService.getUserDetailFromToken(token);
        return ResponseEntity.ok(ObjectResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKey.GETID_SUCCESSFULLY))
                .items(UserResponse.fromUser(user))
                .build());
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getDetailUserFormId(
            @PathVariable("id") long idUser) throws Exception {
        User user = new User();
        user = userService.getUserDetailFromId(idUser);
        return ResponseEntity.ok(ObjectResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKey.GETID_SUCCESSFULLY))
                .items(UserResponse.fromUser(user))
                .build());
    }

    @PostMapping("/check")
    public ResponseEntity<?> checkToken(@RequestHeader("Authorization") String authorizationHeader) {
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
            @Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
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
