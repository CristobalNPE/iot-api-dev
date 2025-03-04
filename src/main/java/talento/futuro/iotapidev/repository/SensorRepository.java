package talento.futuro.iotapidev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import talento.futuro.iotapidev.model.Sensor;

public interface SensorRepository extends JpaRepository<Sensor,Integer> {
}
