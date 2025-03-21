package talento.futuro.iotapidev.exception;

public class DuplicatedLocationException extends RuntimeException {
    public DuplicatedLocationException(String message) {
        super("Location with name %s already exists.".formatted(message));
    }


}
