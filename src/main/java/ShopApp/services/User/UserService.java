/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.services.User;

import ShopApp.services.Role.RoleService;
import ShopApp.components.JwtTokenUtils;
import ShopApp.components.LocalizationUtils;
import ShopApp.services.User.IUserService;
import ShopApp.dtos.UserDTO;
import ShopApp.exception.DataNotFoudException;
import ShopApp.models.Role;
import ShopApp.models.User;
import ShopApp.configurations.*;
import ShopApp.controllers.UserController;
import ShopApp.dtos.AdminUpdateUserDTO;
import ShopApp.dtos.UserLoginDTO;
import ShopApp.dtos.UserUpdateDTO;
import ShopApp.models.Token;

import ShopApp.repositories.RoleRepository;
import ShopApp.repositories.TokenRepository;
import ShopApp.repositories.UserRepository;
import ShopApp.responses.UserResponse;
import ShopApp.utils.MessageKey;
import static ShopApp.utils.ValidationUtils.isValidEmail;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.client.HttpClientErrorException;

/**
 *
 * @author mac
 */
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder; // inject vao
    private final JwtTokenUtils jwtTokenUtil;
    private final AuthenticationManager authManager;
    private final LocalizationUtils localizationUtils;
    private final TokenRepository tokenRepository;
    
    @Override
    @Transactional
    public User createUser(UserDTO userDTO) throws Exception {
        User newUser = new User();
        String phone = userDTO.getPhoneNumber();
        String email = userDTO.getEmail();
        if (userRepository.existsByPhoneNumber(phone)) {
            throw new DataIntegrityViolationException(localizationUtils.getLocalizedMessage(MessageKey.PHONE_NUMBER_EXITS));
        }
        if (userRepository.existsByEmail(email)) {
            throw new DataIntegrityViolationException(localizationUtils.getLocalizedMessage(MessageKey.EMAIL_EXITS));
        }
        
        long idrole_user = 2;
        Role role = roleService.getRoleByid(idrole_user);
        newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .active(true)
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .role(role)
                .build();

        // kiểm tra nếu có accountId, thì không yêu cầu password
        if (userDTO.getFacebookAccountId() == 0 && userDTO.getGoogleAccountId() == 0) {
            String password = userDTO.getPassword();
            // password chua duoc ma hoa
            String encodePassword = passwordEncoder.encode(password);
            newUser.setPassword(encodePassword);
        }
        return userRepository.save(newUser);
    }

    @Override
    public String login(UserLoginDTO userLoginDTO) throws Exception {
        Optional<User> optionalUser = Optional.empty();
        String subject = null;
        // Check if the user exists by phone number
        if (userLoginDTO.getPhoneNumber() != null && !userLoginDTO.getPhoneNumber().isBlank()) {
            optionalUser = userRepository.findByPhoneNumber(userLoginDTO.getPhoneNumber());
            subject = userLoginDTO.getPhoneNumber();
        }
        // If the user is not found by phone number, check by email
        if (optionalUser.isEmpty() && userLoginDTO.getEmail() != null) {
            optionalUser = userRepository.findByEmail(userLoginDTO.getEmail());
            subject = userLoginDTO.getEmail();
        }
        if (optionalUser.isEmpty()) {
            throw new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.PASSWORD_USERNAME_ISCONNECT));
        }

//      return optionalUser.get(); 
        User user = optionalUser.get();
        
//      check isActi Account
        if (user.isActive() == false) {
            throw new BadCredentialsException(localizationUtils.getLocalizedMessage(MessageKey.ACCOUNT_ISACTIVE));
        }
//      checkpassword
        if (user.getFacebookAccountId() == 0 && user.getGoogleAccountId() == 0) {
            if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
                throw new BadCredentialsException(localizationUtils.getLocalizedMessage(MessageKey.PASSWORD_USERNAME_ISCONNECT));
            }
        }

//      auth java spring security
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                subject, userLoginDTO.getPassword(), user.getAuthorities());
        authManager.authenticate(usernamePasswordAuthenticationToken);
        return jwtTokenUtil.generateToken(user);
    }

    @Override
    public Page<User> getAllUser(PageRequest pageRequest) {
        return userRepository.findAll(pageRequest);
    }

    @Override
    @Transactional
    public void blockOrEnableUser(long id, boolean active) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));

        user.setActive(active);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUserAdmin(long id, AdminUpdateUserDTO userUpdateDTO) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));

        Role role = roleService.getRoleByid(userUpdateDTO.getRoleId());
        
        String fullname = userUpdateDTO.getFullName();
        if (fullname != null && !fullname.isEmpty() && fullname != " " && !fullname.equals(user.getFullName())) {
            user.setFullName(userUpdateDTO.getFullName());
        }
        String address = userUpdateDTO.getAddress();
        if (address != null && !address.isEmpty() && address != " " && !address.equals(user.getAddress())) {
            user.setAddress(userUpdateDTO.getAddress());
        }
        
        if (userUpdateDTO.getDateOfBirth() != null) {
            user.setDateOfBirth(userUpdateDTO.getDateOfBirth());
        }

        if (userUpdateDTO.getFacebookAccountId() > 0) {
            user.setFacebookAccountId(userUpdateDTO.getFacebookAccountId());
        }

        if (userUpdateDTO.getGoogleAccountId() > 0) {
            user.setGoogleAccountId(userUpdateDTO.getGoogleAccountId());
        }

        String password = userUpdateDTO.getPassword();
        if (password != null && !password.isEmpty()) {
            // Password chưa được mã hóa
            String encodePassword = passwordEncoder.encode(password);
            user.setPassword(encodePassword);
        }
        
        long roleId = userUpdateDTO.getRoleId();
        if (roleId != user.getRole().getId()) {
            user.setRole(role);
        }
        
        boolean active = userUpdateDTO.isActive();
        if (user.isActive() != active) {
            user.setActive(userUpdateDTO.isActive());
        }

        User newUser = userRepository.save(user);
        
        List<Token>tokens = tokenRepository.findByUser(user);
        for(Token token: tokens){
            tokenRepository.delete(token);
        }
        
        return newUser;
    }

    @Override
    public User getUserDetailFromToken(String token) throws Exception {
        if (jwtTokenUtil.isTokenExpired(token)) {
            throw new Exception("Mời bạn đăng nhập lại.");
        }
        String subject = jwtTokenUtil.extractPhoneNumberOrEmail(token);
        Optional<User> user;
        user = userRepository.findByPhoneNumber(subject);
        if (user.isEmpty() && isValidEmail(subject)) {
            user = userRepository.findByEmail(subject);
        }
        return user.orElseThrow(() -> new Exception("User not found"));

    }
    
    
    @Override
    public User getUserDetailsFromRefreshToken(String token) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(token);
        return getUserDetailFromToken(existingToken.getToken());
    }
    
    @Override
    public User getUserDetailFromId(long id) throws Exception {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoudException("Không tìm thấy user có id: "+ id));
    }

    @Override
    public User updateUser(long id, UserUpdateDTO userUpdateDTO) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));
        
        String fullname = userUpdateDTO.getFullName();
        if (fullname != null && !fullname.isEmpty() && fullname != " " && !fullname.equals(user.getFullName())) {
            user.setFullName(userUpdateDTO.getFullName());
        }
        String address = userUpdateDTO.getAddress();
        if (address != null && !address.isEmpty() && address != " " && !address.equals(user.getAddress())) {
            user.setAddress(userUpdateDTO.getAddress());
        }
        
        if (userUpdateDTO.getDateOfBirth() != null) {
            user.setDateOfBirth(userUpdateDTO.getDateOfBirth());
        }

        if (userUpdateDTO.getFacebookAccountId() > 0) {
            user.setFacebookAccountId(userUpdateDTO.getFacebookAccountId());
        }

        if (userUpdateDTO.getGoogleAccountId() > 0) {
            user.setGoogleAccountId(userUpdateDTO.getGoogleAccountId());
        }

        String password = userUpdateDTO.getPassword();
        if (password != null && !password.isEmpty()) {
            // Password chưa được mã hóa
            String encodePassword = passwordEncoder.encode(password);
            user.setPassword(encodePassword);
        }

        return userRepository.save(user);
    }

    @Override
    public Page<UserResponse> searchUsers(String keyword, PageRequest pageRequest) {
        Page<User> pageUsers;
        pageUsers = userRepository.searchUser(keyword, pageRequest);
        return pageUsers.map(UserResponse::fromUser);
    }




}
