package finalproject.javaee.controller;

import finalproject.javaee.dto.MessageDTO;
import finalproject.javaee.model.pojo.Post;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.PostRepository;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.model.util.exceptions.BaseException;
import finalproject.javaee.model.util.exceptions.usersExceptions.ExistException;
import finalproject.javaee.model.util.exceptions.usersExceptions.UserRelationException;
import finalproject.javaee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class TagController extends BaseController {

    @Autowired private PostRepository postRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;

    @GetMapping(value = "/tags/posts/{postId}/users/{userId}")
    public MessageDTO addTagUser(@PathVariable("postId") long postId,
                                 @PathVariable("userId") long userId, HttpSession session) throws Exception {
        User user = userRepository.findById(userId);
        Post post = postRepository.findById(postId);
        validateisLoggedIn(session);
            validateIfPostExist(postId);
            userService.validateIfUserExist(userId);
            if(!(post.getTagUser().contains(user) && user.getTagPost().contains(post))){
                post.getTagUser().add(user);
                user.getTagPost().add(post);
                userRepository.save(user);
            }else {
                throw new UserRelationException("User already tagged in the post.");
            }
        return new MessageDTO(user.getUsername() + " is tagged in post with id " + post.getId());
    }

    @DeleteMapping(value = "/tags/posts/{postId}/users/{userId}")
    public MessageDTO removeTagUser(@PathVariable("postId") long postId,
                                    @PathVariable("userId") long userId, HttpSession session) throws Exception {
        User user = userRepository.findById(userId);
        Post post = postRepository.findById(postId);

        validateisLoggedIn(session);
            validateIfPostExist(postId);
            userService.validateIfUserExist(userId);
            if(post.getTagUser().contains(user) && user.getTagPost().contains(post)){
                post.getTagUser().remove(user);
                user.getTagPost().remove(post);
                userRepository.save(user);
            }else {
                throw new UserRelationException("User is not tagged in the post.");
            }
        return new MessageDTO(user.getUsername() + " is removed from tag in post with id " + post.getId());
    }


    /* ************* Validations ************* */

    private void validateIfPostExist(long postId)throws BaseException {
        if(!postRepository.existsById(postId)) {
            throw new ExistException("Post doesn't exist");
        }
    }
}
