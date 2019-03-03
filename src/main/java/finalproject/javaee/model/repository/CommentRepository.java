package finalproject.javaee.model.repository;

import finalproject.javaee.model.pojo.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment findById(long id);
    List<Comment> findAllByPostId(long id);

}

