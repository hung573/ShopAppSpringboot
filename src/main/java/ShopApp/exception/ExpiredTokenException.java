/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.exception;

/**
 *
 * @author mac
 */
public class ExpiredTokenException extends Exception{
    public ExpiredTokenException(String message){
        super(message);
    }
}
