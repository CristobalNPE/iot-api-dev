package talento.futuro.iotapidev.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import talento.futuro.iotapidev.dto.SensorDataResponse;
import talento.futuro.iotapidev.model.SensorData;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SensorDataMapper {

    private final ObjectMapper objectMapper;

    public SensorDataResponse toResponse(SensorData sensorData) {
        Map<String, Object> dataMap = objectMapper.convertValue(
                sensorData.getData(),
                new TypeReference<>() {
                }); // type-erasure

        return new SensorDataResponse(
                sensorData.getSensor().getId(),
                dataMap
        );
    }
}
