package talento.futuro.iotapidev.dto;

import jakarta.validation.constraints.NotBlank;

public record LocationRequest(
        @NotBlank String name,
        @NotBlank String country,
        @NotBlank String city,
        @NotBlank String meta
) {
}
