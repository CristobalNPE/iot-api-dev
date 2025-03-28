package talento.futuro.iotapidev.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LocationAdminRequest(
        @NotNull Integer companyId,
        @NotBlank(message = "Name can't be empty") String name,
        @NotBlank(message = "Country can't be empty") String country,
        @NotBlank(message = "City can't be empty")  String city,
        @NotBlank(message = "Meta can't be empty")  String meta

) {
}
