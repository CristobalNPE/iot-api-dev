package talento.futuro.iotapidev.repository;

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
    List<Sensor> getAllSensorsForCompany(Integer companyId);


    @Query("""
            select s from Sensor s
            join s.location l
            where l.company.id = :companyId
            and s.id = :sensorId
            """)
    Optional<Sensor> findSensorByIdForCompany(Integer sensorId, Integer companyId);

    boolean existsByName(String name);


    boolean existsByNameAndId(String name, Integer id);

}
