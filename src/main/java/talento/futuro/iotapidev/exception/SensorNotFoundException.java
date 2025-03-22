package talento.futuro.iotapidev.exception;

public class SensorNotFoundException extends RuntimeException {
    public SensorNotFoundException(Integer id) {
        super("Sensor with id %d not found".formatted(id));
    }
}
