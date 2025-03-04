package talento.futuro.iotapidev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import talento.futuro.iotapidev.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
}
