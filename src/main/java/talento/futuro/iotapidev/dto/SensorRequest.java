package talento.futuro.iotapidev.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SensorRequest(
        @NotNull Integer locationId,
        @NotBlank String sensorName,
        @NotBlank String sensorCategory,
        @NotBlank String sensorMeta
) {
}
