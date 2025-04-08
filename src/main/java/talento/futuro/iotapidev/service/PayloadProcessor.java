package talento.futuro.iotapidev.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.MethodArgumentNotValidException;
import talento.futuro.iotapidev.dto.Payload;
import talento.futuro.iotapidev.exception.InvalidJSONException;
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

    public void extractSensorData(String message) {
        Payload parsedPayload;

        try {
            parsedPayload = objectMapper.readValue(message, Payload.class);
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
            if (datetime == null || datetime.isNull()) {
                throw new IllegalArgumentException();
            }
            SensorData sensorData = new SensorData();
            sensorData.setTimestamp(datetime.asLong());
            sensorData.setData(measurement);

            sensorData.setSensor(sensor);
            sensor.getSensorData().add(sensorData);

        }
    }
}
