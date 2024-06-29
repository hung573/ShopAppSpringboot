/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.services;

import ShopApp.iservices.IUserService;
import ShopApp.dtos.UserDTO;
import ShopApp.exception.DataNotFoudException;
import ShopApp.models.Role;
import ShopApp.models.User;
import ShopApp.repositories.RoleRepository;
import ShopApp.repositories.UserRepository;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/**
 *
 * @author mac
 */
@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
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
                newUser.setPassword(password);
            }
        } catch (DataNotFoudException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userRepository.save(newUser);
    }

    @Override
    public String login(String phone, String password) {
        
        return null;
    }
    
}
