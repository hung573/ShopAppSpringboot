/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.components;

import ShopApp.dtos.sendEmail.SendEmailDTO;
import ShopApp.dtos.sendEmail.ToDTO;
import ShopApp.exception.DataNotFoudException;
import ShopApp.models.Category;
import ShopApp.models.Order;
import ShopApp.responses.OrderResponse;
import ShopApp.services.SendEmail.SendEmailOrderService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

/**
 *
 * @author mac
 */
@Component
@KafkaListener(id = "groupA", topics = { 
    "get-all-categories",
    "insert-a-category",
    "send-email-order-confrim" })
@RequiredArgsConstructor
public class MyKafkaListener {
    private final SendEmailOrderService sendEmailOrderService;
    private final SpringTemplateEngine templateEngine;
    
    @KafkaHandler
    public void listenCategory(Category category) {
        System.out.println("Received: " + category);
    }

    @KafkaHandler(isDefault = true)
    public void unknown(Object object) {
        System.out.println("Received unknown: " + object);
    }
    
    @KafkaHandler
    public void listenListOfCategories(List<Category> categories) {
        System.out.println("Received: " + categories);
    }
    
    @KafkaHandler
    public void listenOrder(OrderResponse order) throws DataNotFoudException {
        System.out.println("Received: " + order);
        sendEmailNotification(order);
    }
    
    private void sendEmailNotification(OrderResponse order) throws DataNotFoudException {
        Context context = new Context();
        context.setVariable("order", order);
        ToDTO toDTO = ToDTO.builder()
                .email(order.getEmail())
                .name(order.getFullName())
                .build();
        String htmlContent = templateEngine.process("order_confirmation", context);
        String subject = "Thank You for Your Order!";
        SendEmailDTO sendEmailDTO = SendEmailDTO.builder()
                .toDTO(toDTO)
                .subject(subject)
                .htmlContent(htmlContent)
                .build();
        sendEmailOrderService.sendEmail(sendEmailDTO);
    }
}
