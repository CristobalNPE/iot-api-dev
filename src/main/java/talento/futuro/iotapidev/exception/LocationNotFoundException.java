package talento.futuro.iotapidev.exception;

public class LocationNotFoundException extends RuntimeException {

    public LocationNotFoundException(Integer id) {
        super("Location with ID %d not found.".formatted(id));
    }

}
