package talento.futuro.iotapidev.consumer;

import jakarta.jms.Message;

public interface MessageConsumer {
    void consume(Message message);
}
