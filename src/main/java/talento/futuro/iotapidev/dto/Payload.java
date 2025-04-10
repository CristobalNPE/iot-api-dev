package talento.futuro.iotapidev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import talento.futuro.iotapidev.validators.TimestampValidator;

import java.util.List;
import java.util.Map;

public record Payload(
        @JsonProperty("api_key")
        @NotBlank(message = "api_key is required")
        String apiKey,

        @JsonProperty("json_data")
        @NotEmpty(message = "json_data cannot be empty")
        List<Map<String, Object>> jsonData
) {
        @AssertTrue(message = "Each json_data item must have a valid 'datetime' Unix timestamp and another metric")
        public boolean isValidJsonData() {
                for (Map<String, Object> item : jsonData) {
                        if (item.size() < 2) {
                                // Must have datetime and another metric
                                return false;
                        }
                        Object datetime = item.get("datetime");
                        if (datetime instanceof Number) {
                                long timestamp = ((Number) datetime).longValue();
                                return TimestampValidator.isValidTimestamp(timestamp);
                        }
                        return false;
                }
                return true;
        }
}
