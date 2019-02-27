package finalproject.javaee.controller;

import finalproject.javaee.model.dao.PostDAO;
import finalproject.javaee.model.pojo.Post;
import finalproject.javaee.model.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    UserController userController;

    @Autowired
    private PostDAO dao;

    private JdbcTemplate jdbcTemplate;

    @GetMapping(value = "/posts/users/{userId}")
    public List<Post> getPostsByUserId(@PathVariable("userId") long id, HttpSession session) {
        //validateLogin(session);
        // jdbcTemplate.query("SELECT description, location_id, categories_id, date FROM posts as p WHERE p.id = id", (resultSet, i) -> toPost(resultSet));
        List<Post> posts = postRepository.findAllById(id);
        return posts;
    }

    @GetMapping(value = "/posts/userId")
    public List<Post> getPostsByUserId(long id){
        List<Post> posts = jdbcTemplate.query("SELECT description, location_id, categories_id, date FROM posts as p WHERE p.id = id", (resultSet, i) -> toPost(resultSet));
        return posts;
    }

    @GetMapping(value = "/posts/{id}")
    public Post getPostByPostId(@PathVariable("id") long id) throws Exception { //TODO own exceprion(Base Exception)
        //validateLogin(session);
        Optional<Post> post = postRepository.findById(id);
        if(post.isPresent()){
            return post.get();
        }
        else{
            throw new Exception("There is no product with such id");
        }
    }

    @GetMapping(value = "/newsfeed/{category}")
    public List<Post> getPostsByCategory(@PathVariable("category") int category_id) throws SQLException{//TODO
        //List<Post> posts = jdbcTemplate.query("SELECT description, location_id, categories_id, date FROM posts as p WHERE p.categories_id = ?", (resultSet, i) -> toPost(resultSet));
        // return posts;
        return dao.getPostsByCategory(category_id);
    }

    private Post toPost(ResultSet resultSet) throws SQLException {
        Post post = new Post(resultSet.getString("description"));
        return post;
    }

    @ExceptionHandler({BadSqlGrammarException.class})
    @ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT)
    public String handleSQLError(){
        return "Sorry bace, sql-a padna. Ne e v nas problema.";
    }

    @ExceptionHandler({Exception.class})
    public String handleStupidError(Exception e){
        return "Sorry bace, she se uvolnqvame s programista. " + e.getClass().getName();
    }


//    @GetMapping(value = "/users/{user}")
//    public ViewUserProfileDTO getAllPostsByUser(@PathVariable("user") long user_id) throws SQLException{
//        List<Post> posts = dao.getPostsByUser(user_id);
//        User u = userController.getUserById(user_id);
//        //TODO access getter for user (VIJ TUUK)!!!
//        //return new ViewUserProfileDTO(, photo, posts);
//    }

    @GetMapping(value = "/newsfeed")
    public List<Post> getAll(HttpSession session){
        return postRepository.findAll();
    }

}
