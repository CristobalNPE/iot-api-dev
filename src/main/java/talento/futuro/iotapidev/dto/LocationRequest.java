package talento.futuro.iotapidev.dto;

public record LocationRequest(
        String name,
        String country,
        String city,
        String meta
) {
}
