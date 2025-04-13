package talento.futuro.iotapidev.consumer;

import jakarta.annotation.PostConstruct;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.stereotype.Service;
import talento.futuro.iotapidev.exception.InvalidJSONException;
import talento.futuro.iotapidev.exception.InvalidMessageTypeException;
import talento.futuro.iotapidev.exception.InvalidSensorApiKeyException;
import talento.futuro.iotapidev.service.PayloadProcessor;

@Slf4j
@Service
@AllArgsConstructor
public class ActiveMQConsumer implements MessageConsumer {

    private final PayloadProcessor payloadProcessor;
    private final SimpleMessageConverter messageConverter;

    @PostConstruct
    public void init() {
        log.info("ActiveMQConsumer Service has started!");
    }

    @JmsListener(destination = "${app.activemq.queue.myQueue}",
            containerFactory = "jmsTransactionalContainerFactory")
    public void getMessageFromQueue(Message message) {
        String stringMessage;
        try {
            stringMessage = messageConverter.fromMessage(message).toString();
        } catch (JMSException e) {
            log.error("Error processing message", e);
            throw new InvalidMessageTypeException();
        }
        try {
            process(stringMessage);
        } catch (InvalidJSONException e) {
            log.warn("Invalid JSON message: {}", stringMessage);
        } catch (InvalidSensorApiKeyException e) {
            log.warn("Message with an invalid API key: {}", stringMessage);
        } catch (Exception e) {
            log.error("Unexpected error processing message: {}", stringMessage, e);
            throw e;
        }
    }

    @Override
    public void process(String message) {
        log.info("\n📧 ActiveMQ Message received: \n{}", message);
        payloadProcessor.extractSensorData(message);
    }

}
