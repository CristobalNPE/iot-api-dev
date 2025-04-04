package talento.futuro.iotapidev.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import talento.futuro.iotapidev.model.Sensor;

import java.util.List;
import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, Integer> {


    @Query("""
            select s from Sensor s
            join s.location l
            where l.company.id = :companyId
            """)
    Page<Sensor> getAllSensorsForCompany(Integer companyId, Pageable pageable);


    @Query("""
            select s from Sensor s
            join s.location l
            where l.company.id = :companyId
            and s.id = :sensorId
            """)
    Optional<Sensor> findSensorByIdForCompany(Integer sensorId, Integer companyId);

    boolean existsByName(String name);


    boolean existsByNameAndId(String name, Integer id);

    Optional<Sensor> findByApiKey(String apiKey);

}
