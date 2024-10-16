/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.dtos.sendEmail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mac
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendEmailDTO {
    
    @JsonProperty("to")
    private ToDTO toDTO;
    
    private String subject;
    private String htmlContent;
}
