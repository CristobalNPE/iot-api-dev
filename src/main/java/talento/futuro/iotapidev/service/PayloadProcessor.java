package talento.futuro.iotapidev.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.MethodArgumentNotValidException;
import talento.futuro.iotapidev.dto.Payload;
import talento.futuro.iotapidev.exception.InvalidJSONException;
import talento.futuro.iotapidev.exception.InvalidMessageTypeException;
import talento.futuro.iotapidev.exception.InvalidSensorApiKeyException;
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

    public void extractSensorData(Payload payload) {
        try {
            String apiKey = payload.apiKey();

            Sensor sensor = sensorRepository.findByApiKey(apiKey)
                                            .orElseThrow(() -> new InvalidSensorApiKeyException(apiKey));

            JsonNode dataArray = objectMapper.valueToTree(payload.jsonData());

            extractMeasurements(dataArray, sensor);

        } catch (IllegalArgumentException e) {
            log.error("Error processing JSON", e);
            throw new InvalidJSONException(e);
        }
    }

    public void extractSensorData(Message message) {
        Payload parsedPayload;

        String messageString = processMessage(message);

        log.info("\n📧 ActiveMQ Message received: \n{}", messageString);

        try {
            parsedPayload = objectMapper.readValue(messageString, Payload.class);
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON", e);
            throw new InvalidJSONException(e);
        }

        try {
            validatePayload(parsedPayload);
        } catch (MethodArgumentNotValidException e) {
            e.getBindingResult().getAllErrors().stream()
             .findFirst()
             .ifPresent(error ->
                     log.error("Error processing JSON: {}", error.getDefaultMessage())
             );
            throw new InvalidJSONException(e);
        }

        extractSensorData(parsedPayload);
    }

    private void validatePayload(Payload payload) throws MethodArgumentNotValidException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(payload, "payload");
        validator.validate(payload, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new MethodArgumentNotValidException(null, bindingResult);
        }
    }

    private void extractMeasurements(JsonNode dataArray, Sensor sensor) throws IllegalArgumentException {

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
                String messageText = textMessage.getText();
                log.info("Received text message: {}", messageText);
                return messageText;
            } else if (message instanceof BytesMessage bytesMessage) {
                long bodyLength = bytesMessage.getBodyLength();
                log.info("Received bytes message: {}", bodyLength);
                byte[] data = new byte[(int) bodyLength];
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
