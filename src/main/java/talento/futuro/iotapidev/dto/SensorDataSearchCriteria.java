package talento.futuro.iotapidev.dto;

import java.util.List;

public record SensorDataSearchCriteria(
        Long from,
        Long to,
        Integer companyId,
        List<Integer> sensorIds
) {
}
