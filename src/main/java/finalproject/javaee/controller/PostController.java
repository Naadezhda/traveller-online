package finalproject.javaee.controller;

import finalproject.javaee.dto.AddPostWithMediaDTO;
import finalproject.javaee.dto.MessageDTO;
import finalproject.javaee.dto.PostWithMediaDTO;
import finalproject.javaee.dto.PostWithUserAndMediaDTO;
import finalproject.javaee.dto.userDTO.PostsByDateComparator;
import finalproject.javaee.dto.userDTO.PostsByLikesComparator;
import finalproject.javaee.dto.userDTO.ViewUserProfileDTO;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.model.util.exceptions.BaseException;
import finalproject.javaee.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

@RestController
public class PostController extends BaseController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserController userController;
    @Autowired
    PostService postService;

    @GetMapping(value = "/posts/users/{userId}")
    public ViewUserProfileDTO getProfileByUserId(@PathVariable("userId") long id, HttpSession session) throws BaseException {
        userController.getLoggedUserByIdSession(session);
        return postService.viewProfile(id);
    }

    @GetMapping(value = "/posts/{id}")
    public PostWithMediaDTO getPostByPostId(@PathVariable("id") long id, HttpSession session) throws BaseException {
        userController.getLoggedUserByIdSession(session);
        return postService.findPostById(id);
    }

    @GetMapping(value = "/newsfeed/categories/{category}")
    public List<PostWithUserAndMediaDTO> getPostsByCategory(@PathVariable("category") int categoryId, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return postService.getAllPostsByCategory(user, categoryId);
    }

    @GetMapping(value = "/profile/users/{user}")
    public ViewUserProfileDTO getUserProfile(@PathVariable("user") long userId, HttpSession session) throws BaseException {
        userController.getLoggedUserByIdSession(session);
        return postService.viewUserProfile(userId);
    }

    public byte[] downloadImage(String mediaName) throws IOException {
        File file = new File(mediaName);
        return Files.readAllBytes(file.toPath());
    }

    @PostMapping(value = "/users/addPost")
    public AddPostWithMediaDTO addPost(@RequestBody AddPostWithMediaDTO dto, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return postService.addUserPost(user, dto);
    }

    @GetMapping(value = "/newsfeed")
    public List<PostWithUserAndMediaDTO> getAll(HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return postService.getAllPostsByFollowings(user);
    }

    public enum Filter {
        LIKES, DATE
    }

    @PostMapping(value = "/newsfeed")
    public List<PostWithUserAndMediaDTO> getAllOrdered(@RequestBody Filter filter, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        List<PostWithUserAndMediaDTO> posts = postService.getAllPostsByFollowings(user);
        switch (filter) {
            case LIKES:
                Collections.sort(posts, new PostsByLikesComparator());
                break;
            case DATE:
                Collections.sort(posts, new PostsByDateComparator());
                break;
        }
        return posts;
    }

    @PostMapping(value = "/posts/{id}/like")
    public MessageDTO likePost(@PathVariable("id") long id, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return postService.likeUserPost(user, id);
    }

    @PostMapping(value = "/posts/{id}/dislike")
    public MessageDTO dislikePost(@PathVariable("id") long id, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return postService.dislikeUserPost(user, id);
    }

}
