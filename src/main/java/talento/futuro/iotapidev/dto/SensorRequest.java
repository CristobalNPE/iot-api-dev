package talento.futuro.iotapidev.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SensorRequest(
        @NotNull(message = "LocationId can't be empty") Integer locationId,
        @NotBlank(message = "SensorName can't be empty") String sensorName,
        @NotBlank(message = "SensorCategory can't be empty") String sensorCategory,
        @NotBlank(message = "SensorMeta can't be empty") String sensorMeta
) {
}
