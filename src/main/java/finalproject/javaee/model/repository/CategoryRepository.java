package finalproject.javaee.model.repository;

import finalproject.javaee.model.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
