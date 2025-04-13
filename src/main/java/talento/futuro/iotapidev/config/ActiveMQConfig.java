package talento.futuro.iotapidev.config;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.SimpleMessageConverter;

@Configuration
@EnableJms
@PropertySource("classpath:activemq.properties")
public class ActiveMQConfig {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.user}")
    private String username;

    @Value("${spring.activemq.password}")
    private String password;

    @Value("${spring.activemq.packages.trust-all:false}")
    private boolean trustAllPackages;

    @Value("${app.activemq.listener.concurrency}")
    private String concurrency;

    @Value("${app.activemq.redelivery.maximum-redeliveries}")
    private int maximumRedeliveries;

    @Value("${app.activemq.redelivery.initial-redelivery-delay}")
    private int initialRedeliveryDelay;

    @Value("${app.activemq.redelivery.redelivery-delay}")
    private int redeliveryDelay;

    @Value("${app.activemq.redelivery.use-exponential-backoff}")
    private boolean useExponentialBackoff;

    @Bean
    public DefaultJmsListenerContainerFactory jmsTransactionalContainerFactory (ConnectionFactory activeMQConnectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(activeMQConnectionFactory);
        factory.setConcurrency(concurrency);
        factory.setSessionTransacted(true);

        return factory;
    }

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(brokerUrl);
        factory.setUserName(username);
        factory.setPassword(password);
        factory.setTrustAllPackages(trustAllPackages);

        RedeliveryPolicy policy = new RedeliveryPolicy();
        policy.setMaximumRedeliveries(maximumRedeliveries);
        policy.setInitialRedeliveryDelay(initialRedeliveryDelay);
        policy.setRedeliveryDelay(redeliveryDelay);
        policy.setUseExponentialBackOff(useExponentialBackoff);

        factory.setRedeliveryPolicy(policy);
        return factory;
    }

    @Bean
    public SimpleMessageConverter simpleMessageConverter() {
        return new SimpleMessageConverter();
    }
}
