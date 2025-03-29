package talento.futuro.iotapidev.repository.specs;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import talento.futuro.iotapidev.dto.SensorDataSearchCriteria;
import talento.futuro.iotapidev.model.SensorData;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SensorDataSpecifications {

    public static Specification<SensorData> withSearchCriteria(SensorDataSearchCriteria criteria) {
        return Specification
                .where(criteria.from() != null ? fromTimestamp(criteria.from()) : null)
                .and(criteria.to() != null ? toTimestamp(criteria.to()) : null)
                .and(criteria.companyId() != null ? withCompanyId(criteria.companyId()) : null)
                .and(criteria.sensorIds() != null && !criteria.sensorIds().isEmpty() ?
                        withSensorIds(criteria.sensorIds()) : null);
    }

    private static Specification<SensorData> fromTimestamp(Long from) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("timestamp"), from);
    }

    private static Specification<SensorData> toTimestamp(Long to) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("timestamp"), to);
    }

    private static Specification<SensorData> withCompanyId(Integer companyId) {
        return (root, query, cb) -> cb.equal(
                root.get("sensor").get("location").get("company").get("id"),
                companyId
        );
    }

    private static Specification<SensorData> withSensorIds(List<Integer> sensorIds) {
        return (root, query, cb) -> root.get("sensor").get("id").in(sensorIds);
    }
}
