package talento.futuro.iotapidev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import talento.futuro.iotapidev.model.Company;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

    Optional<Company> findByApiKey(String apiKey);

    boolean existsByName(String name);
}
