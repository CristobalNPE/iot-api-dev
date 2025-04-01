package talento.futuro.iotapidev.utils;

import talento.futuro.iotapidev.dto.Payload;
import talento.futuro.iotapidev.dto.SensorDataResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SensorDataTestDataFactory {

    public static Payload createValidSuccessPayload(String sensorApiKey) {
        long nowSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        List<Map<String, Object>> jsonData = List.of(
                Map.of("datetime", nowSeconds, "temperature", 25.5, "humidity", 60.1),
                Map.of("datetime", nowSeconds - 10, "temperature", 25.4)
        );
        return new Payload(sensorApiKey, jsonData);
    }

    public static Payload createValuePayload(String sensorApiKey) {
        long nowSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        List<Map<String, Object>> jsonData = List.of(
                Map.of("datetime", nowSeconds, "value", 100)
        );
        return new Payload(sensorApiKey, jsonData);
    }

    public static List<SensorDataResponse> createSensorDataResponseList(List<Integer> sensorIds, long baseTimestamp, int countPerSensor) {
        List<SensorDataResponse> responses = new ArrayList<>();
        for (Integer sensorId : sensorIds) {
            for (int i = 0; i < countPerSensor; i++) {
                long timestamp = baseTimestamp + (sensorId * 100) + (i * 10L);
                Map<String, Object> data = Map.of(
                        "datetime", timestamp,
                        "temperature", 20.0 + sensorId + (i * 0.1), //small variance
                        "humidity", 50.0 + sensorId - (i * 0.5)     //small variance
                );
                responses.add(new SensorDataResponse(sensorId, data));
            }
        }
        return responses;
    }
}
