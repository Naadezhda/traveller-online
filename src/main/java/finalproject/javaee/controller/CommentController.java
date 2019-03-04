package finalproject.javaee.controller;
import finalproject.javaee.model.repository.CommentRepository;
import finalproject.javaee.model.repository.PostRepository;
import finalproject.javaee.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class CommentController extends BaseController{

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserController userController;

    /*@PostMapping(value = "/posts/{id}/comment")
    public UserCommentDTO postComment(@PathVariable("id") long id, @RequestBody String comment, HttpSession session) throws BaseException {
        if(UserController.isLoggedIn(session)) {
            User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
            Post post = postRepository.findById(id);
            Comment com = new Comment(user.getId(), post.getId(), comment);
            commentRepository.save(com);
            return new UserCommentDTO(user.getUsername(), user.getPhoto(), comment);
        }
        else throw new NotLoggedException();
    }

    @DeleteMapping(value = "/posts/{id}/comments/{commentId}")
    public void deleteComment(@PathVariable("id") long id, @PathVariable long commentId, HttpSession session) throws BaseException{
        if(UserController.isLoggedIn(session)) {
            User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
            Comment com = commentRepository.findById(commentId);
            if(commentRepository.findAllByPostId(id).contains(com)){
                if(com.getUserId() == user.getId()){
                    commentRepository.delete(com);
                }
                else throw new IllegalCommentException("Cannot delete other's comments");
            }
            else throw new IllegalCommentException("No such a comment");
        }
        else throw new NotLoggedException();
    }

    @PostMapping(value = "/posts/{postId}/comments/{commentId}/like")
    public void likeComment(@PathVariable("postId") long postId, @PathVariable("commentId") long commentId, HttpSession session) throws BaseException {
        if(UserController.isLoggedIn(session)) {
            User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
            Comment comment = commentRepository.findById(commentId);
            if (!comment.getUsersWhoLiked().contains(user)){
                if(comment.getPostId() == postId) {
                    comment.getUsersWhoLiked().add(user);
                    user.getLikedComments().add(comment);
                    userRepository.save(user);
                }
                else throw new PostExistException("This post doest't have such a comment");
            }
            else throw new LikedPostException("Already liked this comment.");
        }
        else throw new NotLoggedException();
    }

    @DeleteMapping(value = "/posts/{postId}/comments/{commentId}/dislike")
    public void dislikeComment(@PathVariable("postId") long postId, @PathVariable("commentId") long commentId, HttpSession session) throws BaseException {
        if(UserController.isLoggedIn(session)) {
            User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
            Comment comment = commentRepository.findById(commentId);
            if (comment.getUsersWhoLiked().contains(user)) {
                if(comment.getPostId() == postId) {
                    comment.getUsersWhoLiked().remove(user);
                    user.getLikedPosts().remove(comment);
                    commentRepository.delete(comment);
                    userRepository.delete(user);
                }
                else throw new PostExistException("This post doest't have such a comment");
            }
            else throw new NotLikedPostException("Not liked this comment.");
        }
        else throw new NotLoggedException();
    }*/

}
