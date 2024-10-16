/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.repositories.httpCilent;

import ShopApp.dtos.sendEmail.EmailDTO;
import ShopApp.responses.SendEmailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 *
 * @author mac
 */
@FeignClient(name = "email-client", url = "https://api.brevo.com/")
public interface EmailCilent {
    @PostMapping(value = "v3/smtp/email", produces = MediaType.APPLICATION_JSON_VALUE)
    SendEmailResponse sendEmail(@RequestHeader("api-key") String apiKey, @RequestBody EmailDTO emailDTO);
    
}
