package talento.futuro.iotapidev.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@ToString
public class SensorDataCreationMessage {

        @JsonProperty("sensor_api_key")
        private String sensorApiKey;

        @JsonProperty("json_data")
        private List<Map<String, Object>> jsonData;
}
