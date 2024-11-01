/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.services.User;

import ShopApp.dtos.UserDTO;
import ShopApp.dtos.AdminUpdateUserDTO;
import ShopApp.dtos.UserUpdateDTO;
import ShopApp.exception.DataNotFoudException;
import ShopApp.models.User;
import ShopApp.responses.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 *
 * @author mac
 */
public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;
    
    User updateUserAdmin(long id, AdminUpdateUserDTO userUpdateDTO) throws Exception;
    
    User updateUser(long id, UserUpdateDTO userUpdateDTO) throws Exception;
    
    String login(String phone, String password) throws Exception;
    
    Page<User> getAllUser(PageRequest pageRequest);
    
    Page<UserResponse> searchUsers(String keyword,PageRequest pageRequest);
    
    void blockOrEnableUser(long id, boolean active) throws Exception;
    
    User getUserDetailFromToken(String token) throws Exception;
    
    User getUserDetailFromId(long id) throws Exception;
    
    User getUserDetailsFromRefreshToken(String token) throws Exception;
    
    

}
