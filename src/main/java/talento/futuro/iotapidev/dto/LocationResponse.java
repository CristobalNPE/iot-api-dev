package talento.futuro.iotapidev.dto;

public record LocationResponse(
        Integer id,
        String name,
        String country,
        String city,
        String meta
) {
}
