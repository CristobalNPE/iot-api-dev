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

    @Value("${app.activemq.pub-sub-domain:false}")
    private Boolean pubSubDomain;

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory (ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency(concurrency);
        factory.setPubSubDomain(pubSubDomain);
        factory.setSessionTransacted(true);

        return factory;
    }
}
