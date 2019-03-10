package finalproject.javaee.controller;

import finalproject.javaee.dto.PostWithMediaDTO;
import finalproject.javaee.dto.userDTO.ViewUserProfileDTO;
import finalproject.javaee.dto.pojoDTO.MediaDTO;
import finalproject.javaee.model.pojo.Media;
import finalproject.javaee.model.pojo.Post;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.MediaRepository;
import finalproject.javaee.model.repository.PostRepository;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.service.PostService;
import finalproject.javaee.util.exceptions.BaseException;
import finalproject.javaee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
public class SearchController extends BaseController {

    @Autowired private UserRepository userRepository;
    @Autowired private UserController userController;
    @Autowired private PostRepository postRepository;
    @Autowired private MediaRepository mediaRepository;
    @Autowired private UserService userService;
    @Autowired private PostService postService;

    @GetMapping(value = "/search/profile/{username}")
    public ViewUserProfileDTO viewProfile(@PathVariable String username, HttpSession session) throws BaseException {
        userController.getLoggedUserByIdSession(session);
        User u = userRepository.findByUsername(username);
        List<Post> posts = postRepository.findAllByUserId(u.getId());
        List<PostWithMediaDTO> postsWithMedia = new ArrayList<>();
        for (Post p : posts) {
            postsWithMedia.add(postToPostWithMediaDTO(p));
        }
        return new ViewUserProfileDTO(u.getUsername(), u.getPhoto(),
                userService.getAllUserFollowing(u),
                userService.getAllUserFollowers(u),
                postsWithMedia);
    }

    @GetMapping(value = "/search/{username}") // works with containing
    public List<String> filterUsernames(@PathVariable String username, HttpSession session) throws BaseException {
        userController.getLoggedUserByIdSession(session);
        return filterByUsername(username);
    }

    public PostWithMediaDTO postToPostWithMediaDTO(Post p){
        List<Media> media = mediaRepository.findAllByPostId(p.getId());
        List<MediaDTO> mediaDtos = postService.listMediaToDTO(media);
        /*for (Media m : media) {
            mediaDtos.add(m.toDTO());
        }*/
        return new PostWithMediaDTO(postService.postToPostDTO(p), mediaDtos);
    }

    public List<String> filterByUsername(String username){
        List<String> response = new ArrayList<>();
        List<User> users = userRepository.findAllByUsernameContaining(username);
        for (User u : users) {
            response.add(u.getUsername());
        }
        return response;
    }

}
