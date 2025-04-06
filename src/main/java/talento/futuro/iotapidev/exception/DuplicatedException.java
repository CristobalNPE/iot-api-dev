package talento.futuro.iotapidev.exception;

public class DuplicatedException extends RuntimeException {

	public DuplicatedException(String entity, String name) {
		super("%s with name %s already exists.".formatted(entity, name));
	}

}
