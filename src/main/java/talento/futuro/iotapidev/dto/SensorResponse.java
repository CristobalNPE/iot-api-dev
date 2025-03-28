package talento.futuro.iotapidev.dto;

public record SensorResponse(
        Integer id,
        Integer locationId,
        String name,
        String category,
        String meta,
        String apiKey
) {
}
