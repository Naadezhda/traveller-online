package finalproject.javaee.model.repository;

import finalproject.javaee.model.pojo.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Location findById(long id);

}
