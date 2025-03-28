package talento.futuro.iotapidev.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import talento.futuro.iotapidev.dto.SensorDataSearchCriteria;
import talento.futuro.iotapidev.model.SensorData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SensorDataSearch {

    private final EntityManager entityManager;

    public Page<SensorData> search(SensorDataSearchCriteria criteria, Pageable pageable) {

        String jpql = """
                SELECT sd
                FROM SensorData sd
                WHERE 
                """;

        String countJpql = """
                SELECT COUNT(DISTINCT sd)
                FROM SensorData sd
                WHERE 
                """;

        List<String> jpqlParts = new ArrayList<>();
        jpqlParts.add("1=1");

        Map<String, Object> parameters = new HashMap<>();

        if (criteria.from() != null) {
            jpqlParts.add("sd.timestamp >= :from");
            parameters.put("from", criteria.from());
        }

        if (criteria.to() != null) {
            jpqlParts.add("sd.timestamp <= :to");
            parameters.put("to", criteria.to());
        }

        if (criteria.companyId() != null) {
            jpqlParts.add("sd.sensor.location.company.id = :companyId");
            parameters.put("companyId", criteria.companyId());
        }

        if (criteria.sensorIds() != null && !criteria.sensorIds().isEmpty()) {
            jpqlParts.add("sd.sensor.id IN :sensorIds");
            parameters.put("sensorIds", criteria.sensorIds());
        }

        String where = String.join(" AND ", jpqlParts);

        // need for pagination
        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql + where, Long.class);
        parameters.forEach(countQuery::setParameter);
        Long total = countQuery.getSingleResult();

        TypedQuery<SensorData> dataQuery = entityManager.createQuery(jpql + where, SensorData.class);
        parameters.forEach(dataQuery::setParameter);

        // pagination config
        dataQuery.setFirstResult((int) pageable.getOffset());
        dataQuery.setMaxResults(pageable.getPageSize());

        List<SensorData> resultList = dataQuery.getResultList();

        return new PageImpl<>(resultList, pageable, total);
    }

//
//    private String order(Pageable pageable) {
//        return pageable.getSort().isSorted()
//                ? pageable.getSort().stream()
//                          .map(order -> "p." + order.getProperty() + " " + order.getDirection())
//                          .collect(Collectors.joining(", ", " ORDER BY ", ""))
//                : "";
//    }

}
