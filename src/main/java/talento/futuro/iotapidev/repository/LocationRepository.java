package talento.futuro.iotapidev.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import talento.futuro.iotapidev.model.Location;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer> {

    Page<Location> findAllByCompanyId(Integer companyId, Pageable pageable);

    Optional<Location> findByIdAndCompanyId(Integer locationId, Integer companyId);

    boolean existsByName(String name);
    
    @Query("""
    	    select count(l) > 0
    	    from Location l
    	    where l.name = :name and l.company.id = :companyId
    	""")
    boolean existsByNameForCompany(String name, Integer companyId);
    
	@Query("""
			select case
				when count(l) > 0
				then true
				else false
				end
			FROM Location l 
			WHERE l.name = :name AND l.id <> :locationId AND l.company.id = :companyId
			""")
	boolean existsByNameAndIdNot(String name, Integer locationId, Integer companyId);

    
}

