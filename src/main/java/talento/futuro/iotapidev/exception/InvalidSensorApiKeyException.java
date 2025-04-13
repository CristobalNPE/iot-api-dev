package talento.futuro.iotapidev.exception;


public class InvalidSensorApiKeyException extends RuntimeException {
    public InvalidSensorApiKeyException(String apikey) {
        super("Invalid API KEY for sensor API KEY: %s.".formatted(obscureApiKey(apikey)));
    }

    private static String obscureApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 6) {
            return "...";
        }
        return apiKey.substring(0, 3) + "..." + apiKey.substring(apiKey.length() - 3);
    }

}

