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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.http.HttpMethod.*;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 *
 * @author mac
 */

@Configuration
//@EnableMethodSecurity
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@EnableWebMvc
@RequiredArgsConstructor
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

                            ).permitAll()
                            
                            //healthcheck
                            .requestMatchers(GET,String.format("%s/healthcheck/**", apiPrefix)).permitAll()
                            
                            //Categories
                            .requestMatchers(GET,String.format("%s/categories/**", apiPrefix)).permitAll()


                            // Products
                            .requestMatchers(GET,String.format("%s/products/**", apiPrefix)).permitAll()
                            
                            .requestMatchers(GET,String.format("%s/products/images/**", apiPrefix)).permitAll()

                            
                            // ProductsIMG  
                            .requestMatchers(GET,String.format("%s/product-images/**", apiPrefix)).permitAll()

                            // Roles
//                            .requestMatchers(GET,String.format("%s/roles/admin/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(GET,String.format("%s/roles/login/**", apiPrefix)).permitAll()
                            
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
