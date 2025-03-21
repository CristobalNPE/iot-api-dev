package talento.futuro.iotapidev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import talento.futuro.iotapidev.model.Admin;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Optional<Admin> findByUsername(String username);

}
