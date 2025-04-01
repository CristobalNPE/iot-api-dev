package talento.futuro.iotapidev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

public record Payload(
        @JsonProperty("api_key")
        @NotBlank(message = "api_key is required")
        String apiKey,

        @JsonProperty("json_data")
        @NotEmpty(message = "json_data cannot be empty")
        List<Map<String, Object>> jsonData
) {}
