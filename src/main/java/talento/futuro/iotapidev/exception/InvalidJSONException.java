package talento.futuro.iotapidev.exception;

public class InvalidJSONException extends RuntimeException {

    public InvalidJSONException(Exception ex) {
        super("Invalid JSON", ex);
    }

    public InvalidJSONException(String msg, Exception ex) {
        super(msg, ex);
    }
}
