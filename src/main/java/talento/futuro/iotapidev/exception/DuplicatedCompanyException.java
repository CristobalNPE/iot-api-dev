package talento.futuro.iotapidev.exception;

public class DuplicatedCompanyException extends RuntimeException{
    public DuplicatedCompanyException(String message) {
        super("Company with name %s already exists.".formatted(message));
    }
}
