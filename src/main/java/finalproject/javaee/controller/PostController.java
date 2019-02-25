package finalproject.javaee.controller;

import finalproject.javaee.model.Category;
import finalproject.javaee.model.Post;
import finalproject.javaee.model.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RestController
public class PostController {

    @Autowired
    private PostRepository postRepository;

    private JdbcTemplate jdbcTemplate;

    @GetMapping(value = "/posts/userId")
    public List<Post> getPostsByUserId(long id){
        List<Post> posts = jdbcTemplate.query("SELECT description, location_id, categories_id, date FROM posts as p WHERE p.id = id", (resultSet, i) -> toPost(resultSet));
        return posts;
    }

    @GetMapping(value = "/newsFeed/{category}")
    public List<Post> getPostsByCategory(@RequestParam("category") Category category){//TODO
        List<Post> posts = jdbcTemplate.query("SELECT description, location_id, categories_id, date FROM posts as p WHERE p.categories_id = category.id", (resultSet, i) -> toPost(resultSet));
        return posts;
    }

    private Post toPost(ResultSet resultSet) throws SQLException {
        Post post = new Post(resultSet.getString("descr"));
        return post;
    }

}
