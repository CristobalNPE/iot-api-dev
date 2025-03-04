package talento.futuro.iotapidev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import talento.futuro.iotapidev.model.SensorData;

public interface SensorDataRepository extends JpaRepository<SensorData,Integer> {
}
