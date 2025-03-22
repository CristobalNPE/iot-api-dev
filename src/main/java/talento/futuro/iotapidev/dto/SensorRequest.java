package talento.futuro.iotapidev.dto;

import jakarta.validation.constraints.NotBlank;

public record SensorRequest(
        @NotBlank Integer locationId,
        @NotBlank String sensorName,
        @NotBlank String sensorCategory,
        @NotBlank String sensorMeta
) {
}
