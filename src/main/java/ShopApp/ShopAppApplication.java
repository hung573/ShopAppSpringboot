package ShopApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients
public class ShopAppApplication {

	public static void main(String[] args) {
            SpringApplication.run(ShopAppApplication.class, args);
	}

}
