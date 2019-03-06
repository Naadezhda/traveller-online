package finalproject.javaee.controller;

import finalproject.javaee.dto.AddPostWithMediaDTO;
import finalproject.javaee.dto.PostWithMediaDTO;
import finalproject.javaee.dto.PostWithUserAndMediaDTO;
import finalproject.javaee.dto.UserCommentDTO;
import finalproject.javaee.dto.userDTO.PostsByDateComparator;
import finalproject.javaee.dto.userDTO.PostsByLikesComparator;
import finalproject.javaee.dto.userDTO.ViewUserProfileDTO;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.model.util.exceptions.BaseException;
import finalproject.javaee.model.util.exceptions.usersExceptions.NotLoggedException;
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
    public List<PostWithUserAndMediaDTO> getPostsByCategory(@PathVariable("category") int categoryId, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return postService.getAllPostsByCategory(user, categoryId);
    }

    @GetMapping(value = "/profile/users/{user}")
    public ViewUserProfileDTO getUserProfile(@PathVariable("user") long user_id, HttpSession session) throws BaseException {
        userController.getLoggedUserByIdSession(session);
        User user = userRepository.findById(user_id);
        return postService.viewUserProfile(user);
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
    public void likePost(@PathVariable("id") long id, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        postService.likeUserPost(user, id);
    }

    @PostMapping(value = "/posts/{id}/dislike")
    public void dislikePost(@PathVariable("id") long id, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        postService.dislikeUserPost(user, id);
    }

    @PostMapping(value = "/posts/{id}/comment")
    public UserCommentDTO postComment(@PathVariable("id") long id, @RequestBody String comment, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return postService.writeComment(user, id, comment);
    }

    @DeleteMapping(value = "/posts/{id}/comments/{commentId}")
    public UserCommentDTO deleteComment(@PathVariable("id") long id, @PathVariable long commentId, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return postService.deleteComment(user, id, commentId);
    }

    @PostMapping(value = "/posts/{postId}/comments/{commentId}/like")
    public void likeComment(@PathVariable("postId") long postId, @PathVariable("commentId") long commentId, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        postService.likeComment(user, postId, commentId);
    }

    @DeleteMapping(value = "/posts/{postId}/comments/{commentId}/dislike")
    public void dislikeComment(@PathVariable("postId") long postId, @PathVariable("commentId") long commentId, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        postService.dislikeComment(user, postId, commentId);
    }


}
