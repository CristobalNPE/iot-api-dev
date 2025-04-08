package talento.futuro.iotapidev.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import talento.futuro.iotapidev.model.Location;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer> {

    Page<Location> findAllByCompanyId(Integer companyId, Pageable pageable);

    Optional<Location> findByIdAndCompanyId(Integer locationId, Integer companyId);

    boolean existsByName(String name);

}
