/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.iservices;

import ShopApp.models.User;

/**
 *
 * @author mac
 */
public interface ITokenService {
    void addToken(User user, String token, boolean isMobileDevice);
}
