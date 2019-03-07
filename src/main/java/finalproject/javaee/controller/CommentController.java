package finalproject.javaee.controller;
import finalproject.javaee.dto.pojoDTO.CommentDTO;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.CommentRepository;
import finalproject.javaee.model.repository.PostRepository;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.model.util.exceptions.BaseException;
import finalproject.javaee.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;

public class CommentController extends BaseController{

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserController userController;
    @Autowired
    CommentService commentService;

    @PostMapping(value = "/comment/posts/{id}")
    public CommentDTO postComment(@PathVariable("id") long id, @RequestBody String comment, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return commentService.writeComment(user, id, comment);
    }

    @DeleteMapping(value = "/comments/{commentId}/posts/{id}")
    public CommentDTO deleteComment(@PathVariable("id") long id, @PathVariable long commentId, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return commentService.deleteComment(user, id, commentId);
    }

    @PostMapping(value = "/comments/{commentId}/like/posts/{id}")
    public void likeComment(@PathVariable("postId") long postId, @PathVariable("commentId") long commentId, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        commentService.likeComment(user, postId, commentId);
    }

    @DeleteMapping(value = "/comments/{commentId}/like/posts/{id}")
    public void dislikeComment(@PathVariable("postId") long postId, @PathVariable("commentId") long commentId, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        commentService.dislikeComment(user, postId, commentId);
    }

}
