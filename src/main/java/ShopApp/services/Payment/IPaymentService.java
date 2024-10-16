/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.services.Payment;

import ShopApp.models.Payment;
import java.util.List;

/**
 *
 * @author mac
 */
public interface IPaymentService {
    List<Payment> getListPayment() throws Exception;
    
    Payment getPaymentById(Long idPayment) throws Exception;
}
