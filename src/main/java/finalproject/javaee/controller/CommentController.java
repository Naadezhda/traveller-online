package finalproject.javaee.controller;
import finalproject.javaee.dto.pojoDTO.CommentDTO;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.util.exceptions.BaseException;
import finalproject.javaee.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
public class CommentController extends BaseController{

    @Autowired private UserRepository userRepository;
    @Autowired private UserController userController;
    @Autowired private CommentService commentService;

    @PostMapping(value = "/comment/posts/{id}")
    public CommentDTO postComment(@PathVariable("id") long id, @RequestBody String comment, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return commentService.writeComment(user, id, comment);
    }

    @DeleteMapping(value = "/comments/{commentId}/posts/{id}")
    public CommentDTO deleteComment(@PathVariable long commentId, @PathVariable("id") long id, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return commentService.deleteComment(user, id, commentId);
    }

    @PostMapping(value = "/comments/{commentId}/like/posts/{id}")
    public void likeComment(@PathVariable("id") long postId, @PathVariable("commentId") long commentId, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        commentService.likeComment(user, postId, commentId);
    }

    @DeleteMapping(value = "/comments/{commentId}/like/posts/{id}")
    public void dislikeComment(@PathVariable("id") long postId, @PathVariable("commentId") long commentId, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        commentService.dislikeComment(user, postId, commentId);
    }

}
