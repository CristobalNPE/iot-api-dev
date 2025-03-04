package talento.futuro.iotapidev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import talento.futuro.iotapidev.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
}
