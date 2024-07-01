/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.iservices;

import ShopApp.dtos.UserDTO;
import ShopApp.exception.DataNotFoudException;
import ShopApp.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 *
 * @author mac
 */
public interface IUserService {
    User createUser(UserDTO userDTO) throws DataNotFoudException;
    
    String login(String phone, String password) throws Exception;
    
    Page<User> getAllUser(PageRequest pageRequest);
    
    void deleteUser(long id) throws Exception
}
