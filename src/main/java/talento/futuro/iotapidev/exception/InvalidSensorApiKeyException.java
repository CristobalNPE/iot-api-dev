package talento.futuro.iotapidev.exception;


public class InvalidSensorApiKeyException extends RuntimeException {
    public InvalidSensorApiKeyException(String apikey) {
        super("Invalid API KEY for sensor: %s.".formatted(apikey));
    }
}
