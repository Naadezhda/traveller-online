package finalproject.javaee.model.repository;

import finalproject.javaee.model.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

    User findUserById(long id);

}
