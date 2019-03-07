package finalproject.javaee.model.repository;

import finalproject.javaee.model.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

    User findByUsername(String username);
    User findByEmail(String email);
    User findById(long id);
    List<User> findAllByFollowingId(long id);
    List<User> findAllByFollowerId(long id);



}
