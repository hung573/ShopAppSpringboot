/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.services;

import ShopApp.components.JwtTokenUtil;
import ShopApp.iservices.IUserService;
import ShopApp.dtos.UserDTO;
import ShopApp.exception.DataNotFoudException;
import ShopApp.models.Role;
import ShopApp.models.User;
import ShopApp.configurations.*;

import ShopApp.repositories.RoleRepository;
import ShopApp.repositories.UserRepository;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


/**
 *
 * @author mac
 */
@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder; // inject vao
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authManager;
    
    @Override
    public User createUser(UserDTO userDTO) {
        User newUser = new User();
        try {
            String phone = userDTO.getPhoneNumber();
            if (userRepository.existsByPhoneNumber(phone)) {
                throw new DataIntegrityViolationException("Số điện thoại đã được đăng ký.");
            }
            newUser = User.builder()
                    .fullName(userDTO.getFullName())
                    .phoneNumber(userDTO.getPhoneNumber())
                    .password(userDTO.getPassword())
                    .address(userDTO.getAddress())
                    .dateOfBirth(userDTO.getDateOfBirth())
                    .facebookAccountId(userDTO.getFacebookAccountId())
                    .googleAccountId(userDTO.getGoogleAccountId())
                    .build();
            Role role =roleRepository.findById(userDTO.getRoleId())
                    .orElseThrow(() -> new DataNotFoudException("Role not found"));
            newUser.setRole(role);
            
            // kiểm tra nếu có accountId, thì không yêu cầu password
            if (userDTO.getFacebookAccountId() == 0 && userDTO.getGoogleAccountId() == 0) {
                String password = userDTO.getPassword();
                // password chua duoc ma hoa
                String encodePassword = passwordEncoder.encode(password);
                newUser.setPassword(encodePassword);
            }
        } catch (DataNotFoudException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userRepository.save(newUser);
    }

    @Override
    public String login(String phoneNumber, String password) throws Exception{
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoudException("Tài khoản hoặc mật khậu không chính xác");
        }
        
//      return optionalUser.get(); 
        User user = optionalUser.get();
//      check isActi Account
        if (user.isActive() == false) {
            throw new BadCredentialsException("Tài khoản này hiện đang bị khoá");
        }
//      checkpassword
        if (user.getFacebookAccountId() == 0 && user.getGoogleAccountId() == 0) {
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BadCredentialsException("Tài Khoản hoặc mật khẩu không chính xác");
            }
        }


//      auth java spring security
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                phoneNumber, password, user.getAuthorities());
        authManager.authenticate(usernamePasswordAuthenticationToken);
        return jwtTokenUtil.generateToken(user);
    }

    @Override
    public Page<User> getAllUser(PageRequest pageRequest) {
        return userRepository.findAll(pageRequest);
    }

    @Override
    public void deleteUser(long id) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoudException("Không tìm thấy User để xoá"));
        
        user.setActive(false);
        userRepository.save(user);
    }
    
}
