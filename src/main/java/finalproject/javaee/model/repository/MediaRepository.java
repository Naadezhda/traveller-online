package finalproject.javaee.model.repository;

import finalproject.javaee.model.pojo.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Long> {

    List<Media> findAllByPostId(long id);
    boolean existsByMediaUrl(String mediaUrl);
    boolean existsByIdAndPostId(long id, long postId);
    List<Media> findAllByMediaUrlEndingWithAndPostId(String extend, long id);
    Media deleteById(long id);


}
