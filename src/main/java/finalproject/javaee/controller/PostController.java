package finalproject.javaee.controller;

import finalproject.javaee.dto.MediaInBytesDTO;
import finalproject.javaee.dto.PostWithMediaDTO;
import finalproject.javaee.dto.userDTO.ViewUserProfileDTO;
import finalproject.javaee.model.dao.PostDAO;
import finalproject.javaee.model.pojo.Media;
import finalproject.javaee.model.pojo.Post;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.MediaRepository;
import finalproject.javaee.model.repository.PostRepository;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.model.util.exceprions.BaseException;
import finalproject.javaee.model.util.exceprions.InvalidPostException;
import finalproject.javaee.model.util.exceprions.usersExceptions.NotLoggedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class PostController extends BaseController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    UserController userController;

    @Autowired
    private PostDAO dao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping(value = "/posts/users/{userId}")
    public List<Post> getPostsByUserId(@PathVariable("userId") long id, HttpSession session) throws NotLoggedException {
        validateLogin(session);
        List<Post> posts = dao.getPostsByUser(id);
        return posts;
    }

    @GetMapping(value = "/posts/{id}")
    public Post getPostByPostId(@PathVariable("id") long id, HttpSession session) throws BaseException {
        validateLogin(session);
        Optional<Post> post = postRepository.findById(id);
        if(post.isPresent()){
            return post.get();
        }
        else{
            throw new InvalidPostException();
        }
    }

    @GetMapping(value = "/newsfeed/categories/{category}")
    public List<Post> getPostsByCategory(@PathVariable("category") int category_id, HttpSession session) throws NotLoggedException{
        validateLogin(session);
        return dao.getPostsByCategory(category_id);
    }

    @Autowired
    UserRepository ur;

    @Autowired
    MediaRepository mediaRepository;

    @GetMapping(value = "/profile/users/{user}")
    public ViewUserProfileDTO getUserProfile(@PathVariable("user") long user_id, HttpSession session) throws NotLoggedException, IOException{
        if(UserController.isLoggedIn(session)) {
            List<Post> posts = dao.getPostsByUser(user_id);
            User u = ur.findById(user_id);
            String username = u.getUsername();
            String photo = u.getPhoto();
            List<PostWithMediaDTO> postWithMedia = new ArrayList<>();
            for (Post p : posts) {
                List<Media> postMedia = mediaRepository.findAllByPostId(p.getId());
                List<MediaInBytesDTO> postMediaBytes = new ArrayList<>();
                for (Media m : postMedia) {
                    postMediaBytes.add(new MediaInBytesDTO(downloadImage(m.getMediaUrl())));
                }
                postWithMedia.add(new PostWithMediaDTO(p, postMediaBytes));
            }
            return new ViewUserProfileDTO(username, photo, postWithMedia);
        }
        throw new NotLoggedException();
    }

    public byte[] downloadImage(String mediaName) throws IOException {
        File file = new File(mediaName);
        return Files.readAllBytes(file.toPath());
    }

    @GetMapping(value = "/newsfeed")
    public List<Post> getAll(HttpSession session) throws NotLoggedException{
        validateLogin(session);
        return postRepository.findAll();
    }

}
