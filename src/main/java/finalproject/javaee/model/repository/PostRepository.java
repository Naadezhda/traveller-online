package finalproject.javaee.model.repository;
import finalproject.javaee.model.pojo.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUserId(long id);
    List<Post> findAllByUserIdAndCategoriesId(long id1, long id2);
    List<Post> findAllByTagUserId(long id);
    boolean existsByIdAndUserId(long id, long postId);
    Post findById(long id);
}