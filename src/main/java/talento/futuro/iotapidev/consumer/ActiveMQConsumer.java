package talento.futuro.iotapidev.consumer;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import talento.futuro.iotapidev.service.MessageProcessorService;

@Service()
@Profile("activemq")
@AllArgsConstructor
@Slf4j
public class ActiveMQConsumer implements MessageConsumer {

    private final MessageProcessorService messageProcessorService;

    @PostConstruct
    public void init() {
        log.info("ActiveMQConsumer Service has started!");
    }

    @Override
    @JmsListener(destination = "${spring.activemq.queue.myQueue}")
    public void consume(String message) {
        log.info("ActiveMQ Message received: " + message);
        try {
            messageProcessorService.processMessage(message);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
