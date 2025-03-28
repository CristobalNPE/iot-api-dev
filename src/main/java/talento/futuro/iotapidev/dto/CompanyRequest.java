package talento.futuro.iotapidev.dto;

import jakarta.validation.constraints.NotNull;

public record CompanyRequest(
		 @NotNull(message = "CompanyName can't be empty")  String companyName
) {
}
