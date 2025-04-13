package talento.futuro.iotapidev.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import talento.futuro.iotapidev.dto.Payload;
import talento.futuro.iotapidev.exception.InvalidJSONException;
import talento.futuro.iotapidev.exception.InvalidMessageTypeException;
import talento.futuro.iotapidev.exception.InvalidSensorApiKeyException;
import talento.futuro.iotapidev.exception.PayloadValidationException;
import talento.futuro.iotapidev.model.Sensor;
import talento.futuro.iotapidev.model.SensorData;
import talento.futuro.iotapidev.repository.SensorRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PayloadProcessor {

    private final Validator validator;
    private final ObjectMapper objectMapper;
    private final SensorRepository sensorRepository;

    public void extractSensorData(@Valid Payload payload) {
        String apiKey = payload.apiKey();
        Sensor sensor = sensorRepository.findByApiKey(apiKey)
                                        .orElseThrow(() -> new InvalidSensorApiKeyException(apiKey));
        JsonNode dataArray;

        try {
            dataArray = objectMapper.valueToTree(payload.jsonData());
        } catch (IllegalArgumentException e) {
            log.error("Invalid JSON data structure in payload", e);
            throw new InvalidJSONException("Failed to process JSON data", e);
        }

        extractMeasurements(dataArray, sensor);
    }

    public void extractSensorData(Message message) {
        Payload parsedPayload = parseMessage(message);

        validatePayload(parsedPayload);
        extractSensorData(parsedPayload);
    }


    private Payload parseMessage(Message message) {
        String messageString = processMessage(message);

        log.info("\n📧 ActiveMQ Message received: \n{}", messageString);

        try {
            return objectMapper.readValue(messageString, Payload.class);
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON", e);
            throw new InvalidJSONException("Failed to parse message", e);
        }
    }

    private void validatePayload(Payload payload) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(payload, "payload");
        validator.validate(payload, bindingResult);

        if (bindingResult.hasErrors()) {
            ObjectError error = bindingResult.getAllErrors().getFirst();
            String errorMsg = error.getDefaultMessage();
            log.error("Error processing JSON: {}", errorMsg);
            throw new PayloadValidationException(errorMsg);
        }
    }

    private void extractMeasurements(JsonNode dataArray, Sensor sensor) {

        for (JsonNode measurement : dataArray) {

            JsonNode datetime = measurement.get("datetime");

            if (datetime == null || datetime.isNull() || !datetime.isNumber()) {
                log.warn("🔸 Invalid 'datetime' field in payload for sensor [{}-ID{}], skipping...",
                        sensor.getName(), sensor.getId());
                continue;
            }
            SensorData sensorData = new SensorData();
            sensorData.setTimestamp(datetime.asLong());
            sensorData.setData(measurement);

            sensorData.setSensor(sensor);
            sensor.getSensorData().add(sensorData);

        }
    }

    private String processMessage(Message message) {
        try {
            if (message instanceof TextMessage textMessage) {
                log.info("Received text message");
                return textMessage.getText();
            } else if (message instanceof BytesMessage bytesMessage) {
                log.info("Received bytes message:");
                byte[] data = new byte[(int) bytesMessage.getBodyLength()];
                bytesMessage.readBytes(data);
                return new String(data);
            } else {
                log.warn("Received message of unsupported type: {}", message.getClass().getName());
                throw new InvalidMessageTypeException();
            }
        } catch (JMSException e) {
            log.error("Error processing message", e);
            throw new InvalidMessageTypeException();
        }
    }
}
