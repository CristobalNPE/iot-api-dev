package talento.futuro.iotapidev.dto;

public record Payload(
        String api_key,
        Object json_data
){}