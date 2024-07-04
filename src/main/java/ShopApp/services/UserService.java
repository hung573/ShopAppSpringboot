/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.services;

import ShopApp.components.JwtTokenUtils;
import ShopApp.components.LocalizationUtils;
import ShopApp.iservices.IUserService;
import ShopApp.dtos.UserDTO;
import ShopApp.exception.DataNotFoudException;
import ShopApp.models.Role;
import ShopApp.models.User;
import ShopApp.configurations.*;
import ShopApp.dtos.UserUpdateDTO;

import ShopApp.repositories.RoleRepository;
import ShopApp.repositories.UserRepository;
import ShopApp.utils.MessageKey;
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
import org.springframework.web.client.HttpClientErrorException;

/**
 *
 * @author mac
 */
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder; // inject vao
    private final JwtTokenUtils jwtTokenUtil;
    private final AuthenticationManager authManager;
    private final LocalizationUtils localizationUtils;

    @Override
    public User createUser(UserDTO userDTO) throws Exception{
        User newUser = new User();
        String phone = userDTO.getPhoneNumber();
        if (userRepository.existsByPhoneNumber(phone)) {
            throw new DataIntegrityViolationException(localizationUtils.getLocalizedMessage(MessageKey.PHONE_NUMBER_EXITS));
        }
        long idrole_user = 2;
        Role role = roleRepository.findById(idrole_user);
        newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
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
    public String login(String phoneNumber, String password) throws Exception {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
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
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BadCredentialsException(localizationUtils.getLocalizedMessage(MessageKey.PASSWORD_USERNAME_ISCONNECT));
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
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));

        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public User updateUser(long id, UserUpdateDTO userUpdateDTO) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));

        Role role = roleRepository.findById(userUpdateDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.ROLE_NOTFOUND)));

        user.setFullName(userUpdateDTO.getFullName());
        user.setAddress(userUpdateDTO.getAddress());
        user.setDateOfBirth(userUpdateDTO.getDateOfBirth());
        user.setFacebookAccountId(user.getFacebookAccountId());
        user.setGoogleAccountId(user.getGoogleAccountId());
        user.setRole(role);
        user.setActive(userUpdateDTO.isActive());

        String password = userUpdateDTO.getPassword();
        // password chua duoc ma hoa
        String encodePassword = passwordEncoder.encode(password);
        user.setPassword(encodePassword);

        return userRepository.save(user);
    }

}
