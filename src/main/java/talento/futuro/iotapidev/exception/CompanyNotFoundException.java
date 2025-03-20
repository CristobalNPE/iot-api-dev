package talento.futuro.iotapidev.exception;

public class CompanyNotFoundException extends RuntimeException {
    public CompanyNotFoundException() {
        super();
    }

    public CompanyNotFoundException(Integer id) {
        super("Company with ID %d not found.".formatted(id));
    }
}
