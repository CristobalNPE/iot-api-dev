package talento.futuro.iotapidev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import talento.futuro.iotapidev.model.Location;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer> {

    List<Location> findAllByCompanyId(Integer companyId);

    boolean existsByName(String name);

}
