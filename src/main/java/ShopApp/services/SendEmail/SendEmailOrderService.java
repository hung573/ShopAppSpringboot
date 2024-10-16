/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.services.SendEmail;

import ShopApp.dtos.sendEmail.EmailDTO;
import ShopApp.dtos.sendEmail.SendEmailDTO;
import ShopApp.dtos.sendEmail.SenderDTO;
import ShopApp.exception.DataNotFoudException;
import ShopApp.repositories.httpCilent.EmailCilent;
import ShopApp.responses.SendEmailResponse;
import feign.FeignException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author mac
 */
@Service
@RequiredArgsConstructor
public class SendEmailOrderService {
    private final EmailCilent emailCilent;
    
    @Value("${email.apiKey}")
    private String apiKey;
//    private String subject = "Thank You for Your Order!";
//    private String htmlContent = "<!DOCTYPE html> <html> <head>   <meta charset=\\\"UTF-8\\\">   <meta name=\\\"viewport\\\" content=\\\"width=device-width, initial-scale=1.0\\\">   <style>     body {       font-family: Arial, sans-serif;       background-color: #f4f4f4;       margin: 0;       padding: 0;     }     .email-container {       width: 100%;       max-width: 600px;       margin: 0 auto;       background-color: #ffffff;       border: 1px solid #dddddd;     }     .email-header {       background-color: #4CAF50;       color: white;       padding: 20px;       text-align: center;     }     .email-body {       padding: 20px;     }     .order-summary {       margin-top: 20px;     }     .order-summary table {       width: 100%;       border-collapse: collapse;     }     .order-summary th, .order-summary td {       padding: 10px;       border: 1px solid #dddddd;       text-align: left;     }     .order-summary th {       background-color: #f2f2f2;     }     .total {       font-weight: bold;       color: #333333;     }     .footer {       text-align: center;       padding: 10px;       background-color: #f4f4f4;       color: #888888;       font-size: 12px;     }   </style> </head> <body>   <div class=\\\"email-container\\\">     <div class=\\\"email-header\\\">       <h1>Thank You for Your Order!</h1>     </div>     <div class=\\\"email-body\\\">       <p>Dear {{customer_name}},</p>       <p>We are pleased to inform you that your order has been successfully placed.</p>       <p><strong>Order ID:</strong> {{order_id}}</p>       <p><strong>Order Date:</strong> {{order_date}}</p>        <div class=\\\"order-summary\\\">         <h3>Order Summary</h3>         <table>           <thead>             <tr>               <th>Product</th>               <th>Quantity</th>               <th>Price</th>               <th>Total</th>             </tr>           </thead>           <tbody>             <!-- Repeat this row for each product -->             <tr>               <td>{{product_name_1}}</td>               <td>{{quantity_1}}</td>               <td>{{price_1}}</td>               <td>{{total_1}}</td>             </tr>             <tr>               <td>{{product_name_2}}</td>               <td>{{quantity_2}}</td>               <td>{{price_2}}</td>               <td>{{total_2}}</td>             </tr>             <!-- End repeat -->           </tbody>           <tfoot>             <tr>               <td colspan=\\\"3\\\" class=\\\"total\\\">Subtotal</td>               <td>{{subtotal}}</td>             </tr>             <tr>               <td colspan=\\\"3\\\" class=\\\"total\\\">Shipping</td>               <td>{{shipping_cost}}</td>             </tr>             <tr>               <td colspan=\\\"3\\\" class=\\\"total\\\">Total</td>               <td>{{total_cost}}</td>             </tr>           </tfoot>         </table>       </div>        <p>If you have any questions or concerns about your order, feel free to contact our customer support.</p>       <p>Thank you for shopping with us!</p>     </div>      <div class=\\\"footer\\\">       <p>&copy; 2024 Your Company. All rights reserved.</p>       <p>1234 Your Street, Your City, Your Country</p>     </div>   </div> </body> </html>";
    
    public SendEmailResponse sendEmail(SendEmailDTO sendEmailDTO) throws DataNotFoudException{
        
        EmailDTO emailDTO = EmailDTO.builder()
                .senderDTO(SenderDTO.builder()
                        .name("ShopApp HungTran")
                        .email("hung95391@gmail.com")
                        .build())
                .toDTOs(List.of(sendEmailDTO.getToDTO()))
                .htmlContent(sendEmailDTO.getHtmlContent())
                .subject(sendEmailDTO.getSubject())
                .build();
        try {
            return emailCilent.sendEmail(apiKey, emailDTO);
        } catch (FeignException e) {
            throw new DataNotFoudException(e.getMessage());
        }
    }
}
