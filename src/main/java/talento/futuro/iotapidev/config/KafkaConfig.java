package talento.futuro.iotapidev.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@Profile("kafka")
@EnableKafka
@PropertySource("classpath:kafka.properties")
public class KafkaConfig {
}
