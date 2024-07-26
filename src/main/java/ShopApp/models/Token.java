/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mac
 */
@Entity
@Table(name = "tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", length = 255)
    private String token;

    @Column(name = "token_type", length = 50)
    private String tokenType;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;
    
    @Column(name = "is_mobile", columnDefinition = "TINYINT(1)")
    private boolean isMobile;

    private boolean revoked;
    private boolean expired;
    
    @Column(name = "refresh_token", length = 255)
    private String refreshToken;
    
    @Column(name = "refresh_expiration_date")
    private LocalDateTime refreshExpirationDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
