/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.configurations;

import ShopApp.filters.JwtTokenFiltern;
import ShopApp.models.Role;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.http.HttpMethod.*;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


/**
 *
 * @author mac
 */

@Configuration
@EnableMethodSecurity
@EnableWebMvc
@RequiredArgsConstructor
@EnableWebSecurity

public class WebSecurityConfig{
    
    private final JwtTokenFiltern jwtTokenFiltern;
    @Value("${api.prefix}")
    private String apiPrefix;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception{
        http
//                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFiltern, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requets ->{
                    requets
                            .requestMatchers(
                            String.format("%s/users/resigter", apiPrefix),
                            String.format("%s/users/login", apiPrefix),
                            String.format("%s/actuator/health", apiPrefix),
                            
                            
                            //swagger
                            //"/v3/api-docs",
                            //"/v3/api-docs/**",
                            "/api-docs",
                            "/api-docs/**",
                            "/swagger-resources",
                            "/swagger-resources/**",
                            "/configuration/ui",
                            "/configuration/security",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/webjars/swagger-ui/**",
                            "/swagger-ui/index.html"

                            )
                            .permitAll()
                            //healthcheck
                            .requestMatchers(GET,String.format("%s/healthcheck/**", apiPrefix)).permitAll()
                            
                            //Categories
                            .requestMatchers(GET,String.format("%s/categories/**", apiPrefix)).permitAll()
                            
                            .requestMatchers(POST,String.format("%s/categories/**", apiPrefix)).hasAnyRole(Role.ADMIN)

                            .requestMatchers(PUT,String.format("%s/categories/**", apiPrefix)).hasAnyRole( Role.ADMIN)

                            .requestMatchers(DELETE,String.format("%s/categories/**", apiPrefix)).hasAnyRole( Role.ADMIN)

                            // Products
                            .requestMatchers(GET,String.format("%s/products/**", apiPrefix)).permitAll()
                            
                            .requestMatchers(GET,String.format("%s/products/images/**", apiPrefix)).permitAll()

                            .requestMatchers(POST,String.format("%s/products/**", apiPrefix)).hasAnyRole( Role.ADMIN)

                            .requestMatchers(PUT,String.format("%s/products/**", apiPrefix)).hasAnyRole( Role.ADMIN)
                            
                            .requestMatchers(DELETE,String.format("%s/products/**", apiPrefix)).hasAnyRole( Role.ADMIN)
                            
                            // ProductsIMG  
                            .requestMatchers(GET,String.format("%s/product-images/**", apiPrefix)).permitAll()
                            
                            .requestMatchers(DELETE,String.format("%s/product-images/delete/**", apiPrefix)).hasAnyRole(Role.ADMIN)

                            
                            // Users
                            .requestMatchers(GET,String.format("%s/users/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            
                            .requestMatchers(POST,String.format("%s/users/check/**", apiPrefix)).permitAll()
                            
                            .requestMatchers(POST,String.format("%s/users/refreshToken/**", apiPrefix)).permitAll()

                            .requestMatchers(PUT,String.format("%s/users/admin/**", apiPrefix)).hasAnyRole( Role.ADMIN)
                            
                            .requestMatchers(PUT,String.format("%s/users/details/**", apiPrefix)).permitAll()

                            .requestMatchers(DELETE,String.format("%s/users/**", apiPrefix)).hasAnyRole( Role.ADMIN)
                            
                            .requestMatchers(POST,String.format("%s/users/details/**", apiPrefix)).permitAll()

                            
                            // Orders
                            .requestMatchers(POST,String.format("%s/orders/**", apiPrefix)).hasAnyRole(Role.USER, Role.ADMIN)
                            
                            .requestMatchers(GET,String.format("%s/orders/user_order/**", apiPrefix)).permitAll()
                            
                            .requestMatchers(GET,String.format("%s/orders/order/**", apiPrefix)).permitAll()
                            
                            .requestMatchers(GET,String.format("%s/orders/get-order-by-keyword**", apiPrefix)).hasAnyRole(Role.ADMIN)

                            .requestMatchers(PUT,String.format("%s/orders/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            
                            .requestMatchers(DELETE,String.format("%s/orders/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            
                            // OrderDetails
                            .requestMatchers(GET,String.format("%s/order_details/**", apiPrefix)).permitAll()

                            .requestMatchers(POST,String.format("%s/order_details/**", apiPrefix)).hasRole(Role.USER)

                            .requestMatchers(PUT,String.format("%s/order_details/**", apiPrefix)).permitAll()
                            
                            .requestMatchers(DELETE,String.format("%s/order_details/**", apiPrefix)).permitAll()

                            // Roles
                            .requestMatchers(GET,String.format("%s/roles/admin/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(GET,String.format("%s/roles/login/**", apiPrefix)).permitAll()
                            
                            // Comments
                            .requestMatchers(POST,String.format("%s/comments/**", apiPrefix)).permitAll()
                            .requestMatchers(PUT,String.format("%s/comments/**", apiPrefix)).hasAnyRole(Role.USER)
                            .requestMatchers(GET,String.format("%s/comments/admin/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            
                            //Roles
                            .requestMatchers(GET,String.format("%s/roles/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(POST,String.format("%s/roles/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(PUT,String.format("%s/roles/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(DELETE,String.format("%s/roles/**", apiPrefix)).hasAnyRole(Role.ADMIN)

                            
                            //Coupons
                            .requestMatchers(GET,String.format("%s/coupons/**", apiPrefix)).permitAll()
                            
                            //Payments
                            .requestMatchers(GET,String.format("%s/payments/**", apiPrefix)).permitAll()

                            .anyRequest().authenticated();

                })
                
                .csrf(AbstractHttpConfigurer::disable);
        http.securityMatcher(String.valueOf(EndpointRequest.toAnyEndpoint()));
//        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
//            @Override
//            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
//                CorsConfiguration configuration = new CorsConfiguration();
//                configuration.setAllowedOrigins(List.of("*"));
//                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
//                configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
//                configuration.setExposedHeaders(List.of("x-auth-token"));
//                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//                source.registerCorsConfiguration("/**", configuration);
//                httpSecurityCorsConfigurer.configurationSource(source);
//            }
//        });
        
        return http.build();

    }

}
