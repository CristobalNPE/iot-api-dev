package talento.futuro.iotapidev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import talento.futuro.iotapidev.model.SensorData;

import java.util.Optional;

public interface SensorDataRepository extends JpaRepository<SensorData, Integer> {

    Optional<SensorData> findBySensor_Location_Company_Id(Integer integer);

    @Query("""
            delete SensorData sd
            where sd.sensor.id = :sensorId
            and exists (
                select 1 from Sensor s
                where s.id = sd.sensor.id
                and s.location.company.id = :companyId
            )
            """)
    @Modifying
    void deleteAllSensorDataForCompanySensor(Integer companyId, Integer sensorId);

}
