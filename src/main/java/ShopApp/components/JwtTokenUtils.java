/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.components;

import ShopApp.exception.InvalidParamExeption;
import ShopApp.models.Token;
import ShopApp.models.User;
import ShopApp.repositories.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;

/**
 *
 * @author mac
 */

@Component
@RequiredArgsConstructor

public class JwtTokenUtils {
    
    private final TokenRepository tokenRepository;
    
    @Value("${jwt.expiration}")
    private int expiration; // thời gian token kết thúc, và phải được lưu trong biến moi trường trong file .yml
    
    @Value("${jwt.secretKey}")
    private String secretKey;
    
    public String generateToken(ShopApp.models.User user) throws Exception{
//      proberties => claims
        Map<String, Object> claims = new HashMap<>();
//        this.generateSecretKey();
        String subject = getSubject(user);
        claims.put("subject", subject);
        claims.put("id", user.getId());
        try {
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                    .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                    .compact();
            return token;
        } catch (Exception e) {
            throw new InvalidParamExeption("Cannot create JWT token becausse: "+ e.getMessage());
        }
    }
    
    private Key getSigninKey(){
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }
    
    private String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32]; // 256-bit key
        random.nextBytes(keyBytes);
        String secretKey = Encoders.BASE64.encode(keyBytes);
        return secretKey;
    }
    
    private static String getSubject(User user) {
        // Determine subject identifier (phone number or email)
        String subject = user.getPhoneNumber();
        if (subject == null || subject.isBlank()) {
            // If phone number is null or blank, use email as subject
            subject = user.getEmail();
        }
        return subject;
    }
    
    // hàm này giải Claims để lấy các proberties
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigninKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    public  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    //check expiration
     public boolean isTokenExpired(String token) {
        Date expirationDate = this.extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }
    
    public String extractPhoneNumberOrEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public boolean validateToken(String token, User userDetails) {
        String subject = extractClaim(token, Claims::getSubject);
        Token existingToken = tokenRepository.findByToken(token);
        if(existingToken == null ||
                    existingToken.isRevoked() == true ||
                    !userDetails.isActive()
            ) {
            return false;
        }
        return (subject.equals(userDetails.getUsername()))
                && !isTokenExpired(token);
    }
}
