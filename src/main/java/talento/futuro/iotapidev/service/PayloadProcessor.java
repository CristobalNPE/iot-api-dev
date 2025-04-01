package talento.futuro.iotapidev.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private final ObjectMapper objectMapper;
    private final SensorRepository sensorRepository;


    public void extractSensorData(Payload payload) {
        try {
            String apiKey = payload.api_key();

            Sensor sensor = sensorRepository.findByApiKey(apiKey)
                    .orElseThrow(() -> new InvalidSensorApiKeyException(apiKey));

            JsonNode dataArray = objectMapper.valueToTree(payload.json_data());

            extractMeasurements(dataArray, sensor);

        } catch (IllegalArgumentException e) {
            log.error("Error processing JSON", e);
            throw new InvalidJSONException(e);
        }
    }

    public void extractSensorData(String message) {
        try {
            Payload payload = objectMapper.readValue(message, Payload.class);
            extractSensorData(payload);
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON", e);
            throw new InvalidJSONException(e);
        }
    }

    private void extractMeasurements(JsonNode dataArray, Sensor sensor) {
        for (JsonNode measurement : dataArray) {

            JsonNode datetime = measurement.get("datetime");

            SensorData sensorData = new SensorData();
            sensorData.setTimestamp(datetime.asLong());
            sensorData.setData(measurement);

            sensorData.setSensor(sensor);
            sensor.getSensorData().add(sensorData);

        }
    }
}
