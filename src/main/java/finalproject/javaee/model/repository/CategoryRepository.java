package finalproject.javaee.model.repository;

import finalproject.javaee.model.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    Category findByCategoryName(String categoryName);
    Category findById(long id);
    boolean existsById(long id);
}
