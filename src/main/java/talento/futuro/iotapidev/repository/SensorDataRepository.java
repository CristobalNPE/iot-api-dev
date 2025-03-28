package talento.futuro.iotapidev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import talento.futuro.iotapidev.model.SensorData;

import java.util.Optional;

public interface SensorDataRepository extends JpaRepository<SensorData,Integer> {

    Optional<SensorData> findBySensor_Location_Company_Id(Integer integer);
}
