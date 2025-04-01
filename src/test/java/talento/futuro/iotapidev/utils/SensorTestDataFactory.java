package talento.futuro.iotapidev.utils;

import talento.futuro.iotapidev.dto.SensorRequest;
import talento.futuro.iotapidev.dto.SensorResponse;

import java.util.List;

import static talento.futuro.iotapidev.utils.TestUtils.generateApiKeyForTests;

public class SensorTestDataFactory {

    private static final String DEFAULT_SENSOR_CATEGORY = "Temperatura";
    private static final String DEFAULT_SENSOR_META = "Sensor de ambiente general";

    public static SensorRequest createDefaultSensorRequest(Integer locationId, String sensorName) {
        return new SensorRequest(
                locationId,
                sensorName,
                DEFAULT_SENSOR_CATEGORY,
                DEFAULT_SENSOR_META);
    }

    public static SensorResponse createDefaultSensorResponse(Integer id, Integer locationId, String sensorName) {
        return new SensorResponse(
                id,
                locationId,
                sensorName,
                DEFAULT_SENSOR_CATEGORY,
                DEFAULT_SENSOR_META,
                generateApiKeyForTests()
        );
    }

    public static SensorResponse createSensorResponseFromRequest(Integer id, SensorRequest request) {
        return new SensorResponse(
                id,
                request.locationId(),
                request.sensorName(),
                request.sensorCategory(),
                request.sensorMeta(),
                generateApiKeyForTests()
        );
    }

    public static List<SensorResponse> createDefaultSensorResponseList() {
        return List.of(
                createDefaultSensorResponse(1, 10, "Sensor Uno"),
                createDefaultSensorResponse(2, 20, "Sensor Dos"),
                createDefaultSensorResponse(3, 30, "Otro Sensor")
        );
    }
}
