/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.configurations;

import ShopApp.filters.JwtTokenFiltern;
import ShopApp.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.http.HttpMethod.*;

/**
 *
 * @author mac
 */

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@EnableWebMvc

public class WebSecurityConfig {
    
    private final JwtTokenFiltern jwtTokenFiltern;
    @Value("${api.prefix}")
    private String apiPrefix;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFiltern, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requets ->{
                    requets
                            .requestMatchers(
                            String.format("%s/users/resigter", apiPrefix),
                            String.format("%s/users/login", apiPrefix)
                            )
                            .permitAll()
                            
                            //Categories
                            .requestMatchers(GET,String.format("%s/categories**", apiPrefix)).hasAnyRole(Role.USER, Role.ADMIN)
                            
                            .requestMatchers(POST,String.format("%s/categories/**", apiPrefix)).hasRole( Role.ADMIN)

                            .requestMatchers(PUT,String.format("%s/categories/**", apiPrefix)).hasRole( Role.ADMIN)

                            .requestMatchers(DELETE,String.format("%s/categories/**", apiPrefix)).hasRole( Role.ADMIN)

                            // Products
                            .requestMatchers(GET,String.format("%s/products/**", apiPrefix)).hasAnyRole(Role.USER, Role.ADMIN)

                            .requestMatchers(POST,String.format("%s/products/**", apiPrefix)).hasRole( Role.ADMIN)

                            .requestMatchers(PUT,String.format("%s/products/**", apiPrefix)).hasRole( Role.ADMIN)
                            
                            .requestMatchers(DELETE,String.format("%s/products/**", apiPrefix)).hasRole( Role.ADMIN)
                            
                            // Users
                            .requestMatchers(GET,String.format("%s/users/**", apiPrefix)).hasRole(Role.ADMIN)

                            .requestMatchers(PUT,String.format("%s/users/**", apiPrefix)).hasRole( Role.ADMIN)
                            
                            .requestMatchers(DELETE,String.format("%s/users/**", apiPrefix)).hasRole( Role.ADMIN)
                            
                            
                            // Orders
                            .requestMatchers(POST,String.format("%s/orders/**", apiPrefix)).hasRole(Role.USER)
                            
                            .requestMatchers(GET,String.format("%s/orders/user_order/**", apiPrefix)).hasRole(Role.USER)
                            
                            .requestMatchers(GET,String.format("%s/orders/order/**", apiPrefix)).hasRole(Role.USER)
                            
                            .requestMatchers(GET,String.format("%s/orders**", apiPrefix)).hasRole(Role.ADMIN)

                            .requestMatchers(PUT,String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)
                            
                            .requestMatchers(DELETE,String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)
                            
                            // OrderDetails
                            .requestMatchers(GET,String.format("%s/order_details/**", apiPrefix)).hasAnyRole(Role.USER, Role.ADMIN)

                            .requestMatchers(POST,String.format("%s/order_details/**", apiPrefix)).hasRole(Role.USER)

                            .requestMatchers(PUT,String.format("%s/order_details/**", apiPrefix)).hasAnyRole(Role.USER, Role.ADMIN)
                            
                            .requestMatchers(DELETE,String.format("%s/order_details/**", apiPrefix)).hasAnyRole(Role.USER, Role.ADMIN)

                            
                            .anyRequest().authenticated();
                });
        
        return http.build();

    }

}
