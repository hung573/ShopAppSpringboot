/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.components;

import java.net.InetAddress;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 *
 * @author mac
 */
@Component
public class CustomHealthCheck implements HealthIndicator{

    @Override
    public Health health() {
         // Implement your custom health check logic here
        try {
            String computerName = InetAddress.getLocalHost().getHostName();
            return Health.up().withDetail("computerName", computerName).build();//code: 200
            //DOWN => 503
        } catch (Exception e) {
            //throw new RuntimeException(e);
            return Health.down()
                    .withDetail("Error", e.getMessage()).build();
        }
    }
    
}
