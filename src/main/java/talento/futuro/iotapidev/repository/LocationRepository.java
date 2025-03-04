package talento.futuro.iotapidev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import talento.futuro.iotapidev.model.Location;

public interface LocationRepository extends JpaRepository<Location, Integer> {
}
