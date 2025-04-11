package talento.futuro.iotapidev.exception;

public class InvalidMessageTypeException extends RuntimeException {
    public InvalidMessageTypeException() {
        super("Received message of unsupported type");
    }
}
