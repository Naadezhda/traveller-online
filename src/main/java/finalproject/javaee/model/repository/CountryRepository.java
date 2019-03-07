package finalproject.javaee.model.repository;

import finalproject.javaee.model.pojo.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {

    Country findById(long id);
    
}
