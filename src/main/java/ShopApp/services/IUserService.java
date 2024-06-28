/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.services;

import ShopApp.dtos.UserDTO;
import ShopApp.models.User;

/**
 *
 * @author mac
 */
public interface IUserService {
    User createUser(UserDTO userDTO);
    
    String login(String phone, String password);
}
