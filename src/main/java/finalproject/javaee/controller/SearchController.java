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
import finalproject.javaee.model.util.exceptions.BaseException;
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

    public PostWithMediaDTO postToPostWithMediaDTO(Post p){
        List<Media> media = mediaRepository.findAllByPostId(p.getId());
        List<MediaDTO> mediaDtos = new ArrayList<>();
        for (Media m : media) {
            mediaDtos.add(m.toDTO());
        }
        return new PostWithMediaDTO(p.toDTO(), mediaDtos);
    }

}
