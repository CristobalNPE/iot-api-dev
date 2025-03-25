package talento.futuro.iotapidev.exception;

public class InvalidJSONException extends RuntimeException {

    public InvalidJSONException(Exception ex) {
        super("Invalid JSON", ex);
    }
}
