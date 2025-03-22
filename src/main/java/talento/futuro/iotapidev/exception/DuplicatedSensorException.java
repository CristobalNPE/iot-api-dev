package talento.futuro.iotapidev.exception;

import jakarta.validation.constraints.NotBlank;

public class DuplicatedSensorException extends RuntimeException {
    public DuplicatedSensorException(@NotBlank String s) {
        super("Sensor already exists with name %s".formatted(s));

    }
}
