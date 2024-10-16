/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.services.Payment;

import ShopApp.components.LocalizationUtils;
import ShopApp.exception.DataNotFoudException;
import ShopApp.models.Payment;
import ShopApp.repositories.PaymentRepository;
import ShopApp.utils.MessageKey;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author mac
 */
@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService{
    
    private final PaymentRepository paymentRepository;
        private final LocalizationUtils localizationUtils;

    
    @Override
    public List<Payment> getListPayment() throws Exception {
        List<Payment> payment = paymentRepository.findAll();
        return payment;
    }

    @Override
    public Payment getPaymentById(Long idPayment) throws Exception {
        return paymentRepository.findById(idPayment)
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));
    }
    
}
