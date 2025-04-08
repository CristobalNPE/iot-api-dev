package talento.futuro.iotapidev.exception;

public class NotFoundException extends RuntimeException {
	
	
    public NotFoundException() {
		super();
	}

	public NotFoundException(String entity, Integer id) {
		super("%s with ID %d not found".formatted(entity,id));
	}

}
