package finalproject.javaee.controller;

import finalproject.javaee.model.pojo.Post;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.PostRepository;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.model.util.exceptions.BaseException;
import finalproject.javaee.model.util.exceptions.usersExceptions.TagException;
import finalproject.javaee.model.util.exceptions.postsExceptions.PostExistException;
import finalproject.javaee.model.util.exceptions.usersExceptions.NotLoggedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class TagController extends BaseController {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserController userController;

    @GetMapping(value = "/tags/posts/{postId}/users/{userId}")
    public void addTagUser(@PathVariable("postId") long postId, @PathVariable("userId") long userId, HttpSession session) throws BaseException {
        User user = userRepository.findById(userId);
        Post post = postRepository.findById(postId);

        if(UserController.isLoggedIn(session)){
            validateIfPostExist(postId);
            userController.validateIfUserExist(userId);
            if(!(post.getTagUser().contains(user) && user.getTagPost().contains(post))){
                post.getTagUser().add(user);
                user.getTagPost().add(post);
                userRepository.save(user);
            }else {
                throw new TagException("User already tagged in the post.");
            }
        }else {
            throw new NotLoggedException();
        }
    }

    @DeleteMapping(value = "/tags/posts/{postId}/users/{userId}")
    public void removeTagUser(@PathVariable("postId") long postId, @PathVariable("userId") long userId, HttpSession session) throws BaseException {
        User user = userRepository.findById(userId);
        Post post = postRepository.findById(postId);

        if(UserController.isLoggedIn(session)){
            validateIfPostExist(postId);
            userController.validateIfUserExist(userId);
            if(post.getTagUser().contains(user) && user.getTagPost().contains(post)){
                post.getTagUser().remove(user);
                user.getTagPost().remove(post);
                userRepository.save(user);
            }else {
                throw new TagException("User is not tagged in the post.");
            }
        }else {
            throw new NotLoggedException();
        }
    }


    /* ************* Validations ************* */

    private void validateIfPostExist(long postId)throws PostExistException {
        if(!postRepository.existsById(postId)) {
            throw new PostExistException("Post doesn't exist");
        }
    }
}
