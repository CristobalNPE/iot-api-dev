package talento.futuro.iotapidev.consumer;

import jakarta.annotation.PostConstruct;
import jakarta.jms.Message;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import talento.futuro.iotapidev.service.PayloadProcessor;

@Slf4j
@Service
@AllArgsConstructor
public class ActiveMQConsumer implements MessageConsumer {

    private final PayloadProcessor payloadProcessor;

    @PostConstruct
    public void init() {
        log.info("ActiveMQConsumer Service has started!");
    }

    @Override
    @JmsListener(destination = "${app.activemq.destination}")
    public void consume(Message message) {
        payloadProcessor.extractSensorData(message);
    }
}
