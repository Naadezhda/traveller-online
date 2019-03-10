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
import finalproject.javaee.util.exceptions.BaseException;
import finalproject.javaee.service.PostService;
import finalproject.javaee.util.exceptions.usersExceptions.ExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@RestController
public class PostController extends BaseController {

    @Autowired private UserRepository userRepository;
    @Autowired private UserController userController;
    @Autowired private PostService postService;

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
    public List<PostWithUserAndMediaDTO> getPostsByCategory(@PathVariable("category") long categoryId, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return postService.getAllPostsByCategory(user, categoryId);
    }

    @GetMapping(value = "/profile/users/{user}")
    public ViewUserProfileDTO getUserProfile(@PathVariable("user") long userId, HttpSession session) throws BaseException {
        userController.getLoggedUserByIdSession(session);
        return postService.viewUserProfile(userId);
    }

    @PostMapping(value = "/users/addPost")
    public PostWithMediaDTO addPost(@RequestBody AddPostWithMediaDTO dto, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return postService.addUserPost(user, dto);
    }

    @DeleteMapping(value = "/users/posts/{id}")
    public MessageDTO deletePost(@PathVariable long id, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return postService.deleteUserPost(user, id);
    }

    @GetMapping(value = "/newsfeed")
    public List<PostWithUserAndMediaDTO> getAll(HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return postService.getAllPostsByFollowings(user);
    }

    @GetMapping(value = "/newsfeed/ordered")
    public List<PostWithUserAndMediaDTO> getAllOrdered(@RequestParam String filter, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        List<PostWithUserAndMediaDTO> posts = postService.getAllPostsByFollowings(user);
        switch (filter) {
            case "likes":
                Collections.sort(posts, new PostsByLikesComparator());
                return posts;
            case "date":
                Collections.sort(posts, new PostsByDateComparator());
                return posts;
        }
        throw new ExistException("There is no such filter!");
    }

    @PostMapping(value = "/posts/{id}/like")
    public MessageDTO likePost(@PathVariable("id") long id, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return postService.likeUserPost(user, id);
    }

    @Transactional
    @PostMapping(value = "/posts/{id}/dislike")
    public MessageDTO dislikePost(@PathVariable("id") long id, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return postService.dislikeUserPost(user, id);
    }

    @GetMapping(value = "/profile/users/{id}/tagged")
    public List<PostWithUserAndMediaDTO> viewTaggedPosts(@PathVariable("id") long id, HttpSession session) throws BaseException {
        userRepository.findById(userController.getLoggedUserByIdSession(session));
        return postService.getTaggedPosts(id);
    }

}
