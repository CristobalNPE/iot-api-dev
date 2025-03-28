package talento.futuro.iotapidev.dto;

import java.util.Map;

public record SensorDataResponse(
        Integer sensorId,
        Map<String, Object> sensorData
) {
}
