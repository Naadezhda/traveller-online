package finalproject.javaee.controller;

import finalproject.javaee.dto.PostWithMediaDTO;
import finalproject.javaee.dto.ViewUserProfileDTO;
import finalproject.javaee.model.dao.PostDAO;
import finalproject.javaee.model.pojo.Media;
import finalproject.javaee.model.pojo.Post;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.MediaRepository;
import finalproject.javaee.model.repository.PostRepository;
import finalproject.javaee.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping(value = "/posts/users/{userId}")
    public List<Post> getPostsByUserId(@PathVariable("userId") long id, HttpSession session) {
        //validateLogin(session);
        List<Post> posts = dao.getPostsByUser(id);
        return posts;
    }

    @GetMapping(value = "/posts/{id}")
    public Post getPostByPostId(@PathVariable("id") long id) throws Exception { //TODO own exception(Base Exception)
        //validateLogin(session);
        Optional<Post> post = postRepository.findById(id);
        if(post.isPresent()){
            return post.get();
        }
        else{
            throw new Exception("There is no product with such id");
        }
    }

    @GetMapping(value = "/newsfeed/categories/{category}")
    public List<Post> getPostsByCategory(@PathVariable("category") int category_id) {
        //validateLogin(session);
        return dao.getPostsByCategory(category_id);
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

    @Autowired
    UserRepository ur;

    @Autowired
    MediaRepository mediaRepository;

    @GetMapping(value = "/profile/users/{user}")
    public ViewUserProfileDTO getUserProfile(@PathVariable("user") long user_id) {
        //validateLogin(session);
        List<Post> posts = dao.getPostsByUser(user_id);
        User u = ur.findById(user_id);
        String username = u.getUsername();
        String photo = u.getPhoto();
        List<PostWithMediaDTO> postWithMedia = new ArrayList<>();
        for(Post p : posts){
            List<Media> postMedia = mediaRepository.findAllByPostId(p.getId());
            postWithMedia.add(new PostWithMediaDTO(p, postMedia));
        }
        return new ViewUserProfileDTO(username, photo, postWithMedia);
    }

    @GetMapping(value = "/newsfeed")
    public List<Post> getAll(HttpSession session){
        //validateLogin(session);
        return postRepository.findAll();
    }

}
