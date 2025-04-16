package talento.futuro.iotapidev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import talento.futuro.iotapidev.model.Admin;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Optional<Admin> findByUsername(String username);

    
    @Query("""
    	    select count(l) > 0
    	    from Location l
    	    where l.name = :name and l.company.id = :companyId
    	""")
    boolean existsByNameForCompany(String name, Integer companyId);
    
}
