package talento.futuro.iotapidev.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import talento.futuro.iotapidev.exception.InvalidJSONException;
import talento.futuro.iotapidev.model.SensorDataCreationMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageProcessorService {

    private final ObjectMapper objectMapper;

    public void processMessage(String message) {
        try {
            SensorDataCreationMessage sensorDataCreationMessage = parseMessage(message);
            log.info("Successfully parsed SensorDataCreationMessage: {}", sensorDataCreationMessage);
        } catch (Exception e) {
            throw new InvalidJSONException(e);
        }
    }

    private SensorDataCreationMessage parseMessage(String message) throws Exception {
        return objectMapper.readValue(message, SensorDataCreationMessage.class);
    }
}
