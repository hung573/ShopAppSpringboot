/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.sql.Date;
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
public class UserDTO {
    @JsonProperty("fullname")
    @NotBlank(message = "Họ và tên không được để tróng")
    private String fullName;
    
    @JsonProperty("phone_number")
    private String phoneNumber;
    
    @JsonProperty("email")
    private String email;
    
    @NotBlank(message = "Địa chỉ không được để tróng")
    private String address;
    
    @NotBlank(message = "Password không được để tróng")
    @Size(min = 3, message = "Password không được đặt dưới 3 ký tự")
    private String password;
    
    @JsonProperty("retype_password")
    @NotBlank(message = "Nhập lại password")
    private String retypePassword;
    
    @JsonProperty("date_of_birth")
    private Date dateOfBirth;
    
    @JsonProperty("facebook_account_id")
    private int facebookAccountId;
    
    @JsonProperty("google_account_id")
    private int googleAccountId;
    
//    @JsonProperty("role_id")
//    @NotNull(message = "Role is not null")
//    private Long roleId;
    
}
