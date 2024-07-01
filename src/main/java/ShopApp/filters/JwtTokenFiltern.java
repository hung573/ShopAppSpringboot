/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.filters;

import ShopApp.components.JwtTokenUtil;
import ShopApp.models.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.*;

/**
 *
 * @author mac
 */
@Component
@RequiredArgsConstructor
public class JwtTokenFiltern extends OncePerRequestFilter{
    
    private final UserDetailsService userDetailsService;
    
    private final JwtTokenUtil jwtTokenUtil;
    
    @Value("${api.prefix}")
    private String apiPrefix;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain) throws ServletException, IOException {
        
        // cho phép được truy cập hết tất cả request
//        filterChain.doFilter(request, response); 
        if (isBypassToken(request)) 
        {
            filterChain.doFilter(request, response); 
            return;
        }
        final String authHead = request.getHeader("Authorization");
        if (authHead != null && authHead.startsWith("Bearer ")) 
        {
            final String token = authHead.substring(7);
            final String phoneNumber = (String) jwtTokenUtil.extractPhoneNumber(token);
            
            if (phoneNumber != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) 
            {
                UserDetails userDetails = userDetailsService.loadUserByUsername(phoneNumber);
                
                if (jwtTokenUtil.validateToken(token, userDetails))
                {
                    UsernamePasswordAuthenticationToken authenticationToken = 
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null, 
                                    userDetails.getAuthorities()
                            );
                    
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response); 
        }
    }
    
    private boolean isBypassToken(@NotNull HttpServletRequest request){
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of(String.format("%s/products", apiPrefix), "GET"),
                Pair.of(String.format("%s/categories", apiPrefix), "GET"),
                Pair.of(String.format("%s/users/resigter", apiPrefix), "POST"),
                Pair.of(String.format("%s/users/login", apiPrefix), "POST")
        );
        for(Pair<String,String> bypassToken: bypassTokens){
            if (request.getServletPath().contains(bypassToken.getFirst()) &&
                request.getMethod().equals(bypassToken.getSecond())) {
                return true;
            }
        }
        return false;
    }
    
}
