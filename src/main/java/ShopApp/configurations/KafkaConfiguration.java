/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.configurations;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 *
 * @author mac
 */
@Configuration
public class KafkaConfiguration {
    @Bean
    public CommonErrorHandler errorHandler(KafkaOperations<Object, Object> template) {
        return new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(template), new FixedBackOff(1000L, 2));
    }

    @Bean
    public NewTopic insertACategoryTopic() {
        return new NewTopic("insert-a-category", 1, (short) 1);
    }
    @Bean
    public NewTopic getAllCategoriesTopic() {
        return new NewTopic("get-all-categories", 1, (short) 1);
    }
    @Bean
    public NewTopic sendOrderSuccessMessageTopic() {
        return new NewTopic("send-email-order-confrim", 1, (short) 1);
    }
}
