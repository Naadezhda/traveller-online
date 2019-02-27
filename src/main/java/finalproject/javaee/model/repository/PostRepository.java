package finalproject.javaee.model.repository;

import finalproject.javaee.model.pojo.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllById(long id);
}
