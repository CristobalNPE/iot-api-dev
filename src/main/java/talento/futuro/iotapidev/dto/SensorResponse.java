package talento.futuro.iotapidev.dto;

public record SensorResponse(
        Integer id,
        String name,
        String category,
        String apiKey
) {
}
