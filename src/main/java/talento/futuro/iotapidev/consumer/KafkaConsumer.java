package talento.futuro.iotapidev.consumer;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import talento.futuro.iotapidev.service.MessageProcessorService;

@Service()
@Profile("kafka")
@AllArgsConstructor
@Slf4j
public class KafkaConsumer implements MessageConsumer {

    private final MessageProcessorService messageProcessorService;

    @PostConstruct
    public void init() {
        log.info("KafkaConsumer Service has started!");
    }

    @Override
    @KafkaListener(topics = "${spring.kafka.queue.myTopic}")
    public void consume(String message) {
        log.info("Kafka Message received: " + message);
        try {
            messageProcessorService.processMessage(message);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
