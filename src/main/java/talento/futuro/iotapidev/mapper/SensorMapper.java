package talento.futuro.iotapidev.mapper;

import org.springframework.stereotype.Component;
import talento.futuro.iotapidev.dto.SensorResponse;
import talento.futuro.iotapidev.model.Sensor;

@Component
public class SensorMapper {

    public SensorResponse toSensorResponse(Sensor sensor) {

        return new SensorResponse(
                sensor.getId(),
                sensor.getLocation().getId(),
                sensor.getName(),
                sensor.getCategory(),
                sensor.getMeta(),
                sensor.getApiKey()
        );

    }

}
