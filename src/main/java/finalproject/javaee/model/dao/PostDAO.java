package finalproject.javaee.model.dao;

import finalproject.javaee.model.pojo.Post;
import finalproject.javaee.model.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostDAO extends BaseDAO{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostRepository postRepository;

    public List<Post> getPostsByCategory(int category_id){
        List<Post> posts = postRepository.findAllByCategoriesId(category_id);
        return posts;
    }

    public List<Post> getPostsByUser(long userId) {
        List<Post> postsByUser = postRepository.findAllByUserId(userId);
        return postsByUser;
    }

}
