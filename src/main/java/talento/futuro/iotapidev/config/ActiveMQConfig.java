package talento.futuro.iotapidev.config;

import jakarta.jms.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

@Configuration
@EnableJms
@PropertySource("classpath:activemq.properties")
public class ActiveMQConfig {

    @Value("${app.activemq.listener.concurrency}")
    private String concurrency;

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory (ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency("1");
        factory.setPubSubDomain(true);
        factory.setSessionTransacted(true);

        return factory;
    }
}
