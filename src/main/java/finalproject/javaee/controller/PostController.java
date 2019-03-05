package finalproject.javaee.controller;

import finalproject.javaee.dto.*;
import finalproject.javaee.dto.userDTO.*;
import finalproject.javaee.dto.MediaInBytesDTO;
import finalproject.javaee.model.dao.PostDAO;
import finalproject.javaee.model.pojo.Comment;
import finalproject.javaee.model.pojo.Media;
import finalproject.javaee.model.pojo.Post;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.CommentRepository;
import finalproject.javaee.model.repository.MediaRepository;
import finalproject.javaee.model.repository.PostRepository;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.model.util.exceptions.BaseException;
import finalproject.javaee.model.util.exceptions.postsExceptions.*;
import finalproject.javaee.model.util.exceptions.usersExceptions.NotLoggedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class PostController extends BaseController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    UserController userController;

    @Autowired
    private PostDAO dao;

    @GetMapping(value = "/posts/users/{userId}")
    public List<Post> getPostsByUserId(@PathVariable("userId") long id, HttpSession session) throws BaseException {
        userController.getLoggedUserByIdSession(session);
        List<Post> posts = dao.getPostsByUser(id);
        return posts;
    }

    @GetMapping(value = "/posts/{id}")
    public Post getPostByPostId(@PathVariable("id") long id, HttpSession session) throws BaseException {
        userController.getLoggedUserByIdSession(session);
        return findPostById(id);
    }

    public Post findPostById(long id) throws BaseException {
        Post post = postRepository.findById(id);
        if(!postRepository.existsById(id)){
            throw new InvalidPostException();
        }
        return post;
    }

    @GetMapping(value = "/newsfeed/categories/{category}")
    public List<PostWithUserAndMediaDTO> getPostsByCategory(@PathVariable("category") int categoryId, HttpSession session) throws BaseException {
        if(UserController.isLoggedIn(session)) {
            User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
            List<ViewUserRelationsDTO> users = userController.getAllUserFollowing(user);
            List<PostWithUserAndMediaDTO> postsByFollowingWithMedia = new ArrayList<>();
            for (ViewUserRelationsDTO u : users) {
                List<Post> postsByFollowing = postRepository.findAllByUserIdAndCategoriesId(u.getId(), categoryId);
                List<PostWithUserAndMediaDTO> postsByUserWithMedia = getPostsByUserWithMedia(u, postsByFollowing);
                postsByFollowingWithMedia.addAll(postsByUserWithMedia);
            }
            return postsByFollowingWithMedia;
        }
        throw new NotLoggedException();
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    MediaRepository mediaRepository;

    @GetMapping(value = "/profile/users/{user}")
    public ViewUserProfileDTO getUserProfile(@PathVariable("user") long user_id, HttpSession session) throws BaseException, IOException{
        userController.getLoggedUserByIdSession(session);
        User u = userRepository.findById(user_id);
        String username = u.getUsername();
        String photo = u.getPhoto();
        return new ViewUserProfileDTO(username, photo,getAllUserPosts(user_id).size(), getAllUserPosts(user_id),
                userController.getAllUserFollowing(u).size(),userController.getAllUserFollowing(u),
                userController.getAllUserFollowers(u).size(),userController.getAllUserFollowers(u));
    }

    protected List<PostWithMediaInBytesDTO> getAllUserPosts(Long id) throws IOException{
        List<Post> posts = dao.getPostsByUser(id);
        List<PostWithMediaInBytesDTO> postWithMedia = new ArrayList<>();
        for(Post p : posts){
            List<Media> postMedia = mediaRepository.findAllByPostId(p.getId());
            List<MediaInBytesDTO> postMediaBytes = new ArrayList<>();
            for(Media m : postMedia){
                postMediaBytes.add(new MediaInBytesDTO(downloadImage(m.getMediaUrl())));
            }
            postWithMedia.add(new PostWithMediaInBytesDTO(p,postMediaBytes));
        }
        return postWithMedia;
    }

    public byte[] downloadImage(String mediaName) throws IOException {
        File file = new File(mediaName);
        return Files.readAllBytes(file.toPath());
    }

    @PostMapping(value = "/users/addPost")
    public void addPost(@RequestBody PostAPostWithMediaDTO dto, HttpSession session) throws BaseException {
        userController.getLoggedUserByIdSession(session);
        User user = ((User) (session.getAttribute("User")));
        Post p = new Post(user.getId(), dto.getDescription(), dto.getLocationId(), dto.getCategoriesId());
        postRepository.save(p);
        Media m = new Media(p.getId(), dto.getMediaUrl());
        mediaRepository.save(m);
    }

    @GetMapping(value = "/newsfeed")
    public List<PostWithUserAndMediaDTO> getAll(HttpSession session) throws BaseException {
        if(UserController.isLoggedIn(session)) {
            User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
            return getAllPostsByFollowings(user);
        }
        throw new NotLoggedException();
    }

    public enum Filter {
        LIKES, DATE
    }

    @PostMapping(value = "/newsfeed")
    public List<PostWithUserAndMediaDTO> getAllOrdered(@RequestBody Filter filter, HttpSession session) throws BaseException {
        userController.getLoggedUserByIdSession(session);
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        List<PostWithUserAndMediaDTO> posts = getAllPostsByFollowings(user);
        switch(filter){
            case LIKES:
               Collections.sort(posts, new PostsByLikesComparator());
               break;
            case DATE:
                Collections.sort(posts, new PostsByDateComparator());
                break;
        }
        return posts;
    }

    public List<PostWithUserAndMediaDTO> getAllPostsByFollowings(User user) {
        List<ViewUserRelationsDTO> users = userController.getAllUserFollowing(user);
        List<PostWithUserAndMediaDTO> allPostsByFollowings = new ArrayList<>();
        for (ViewUserRelationsDTO u : users) {
            List<Post> postsByFollowing = postRepository.findAllByUserId(u.getId());
            allPostsByFollowings.addAll(getPostsByUserWithMedia(u, postsByFollowing));
        }
        return allPostsByFollowings;
    }

    public List<PostWithUserAndMediaDTO> getPostsByUserWithMedia(ViewUserRelationsDTO u, List<Post> postsByFollowing) {
        List<PostDTO> posts = new ArrayList<>();
        List<PostWithUserAndMediaDTO> postsByUser = new ArrayList<>();
        for (Post p : postsByFollowing) {
            PostDTO dto = new PostDTO(p.getId(), p.getDescription(), p.getLocationId(), p.getCategoriesId());
            posts.add(dto);
            List<Media> media = mediaRepository.findAllByPostId(dto.getId());
            postsByUser.add(new PostWithUserAndMediaDTO(u.getUsername(), u.getPhoto(), p.getDate(), new PostWithMediaURL(dto, media), p.getUsersWhoLiked().size(), p.getUsersWhoLikedInDTO()));
        }
        return postsByUser;
    }

    @PostMapping(value = "/posts/{id}/like")
    public void likePost(@PathVariable("id") long id, HttpSession session) throws BaseException {
        if(UserController.isLoggedIn(session)) {
            User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
            Post post = findPostById(id);
            if (!post.getUsersWhoLiked().contains(user)) {
                post.getUsersWhoLiked().add(user);
                user.getLikedPosts().add(post);
                userRepository.save(user);
            }
            else{
                throw new LikedPostException("Already liked this post.");
            }
        }
        else throw new NotLoggedException();
    }

    @PostMapping(value = "/posts/{id}/dislike")
    public void dislikePost(@PathVariable("id") long id, HttpSession session) throws BaseException {
        if(UserController.isLoggedIn(session)) {
            User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
            Post post = findPostById(id);
            if (post.getUsersWhoLiked().contains(user)) {
                post.getUsersWhoLiked().remove(user);
                user.getLikedPosts().remove(post);
                postRepository.save(post);
                userRepository.save(user);
            }
            else{
                throw new NotLikedPostException("Not liked this post.");
            }
        }
        else throw new NotLoggedException();
    }


    @PostMapping(value = "/posts/{id}/comment")
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

    @Autowired
    CommentRepository commentRepository;

    @DeleteMapping(value = "/posts/{postId}/comments/{commentId}/dislike")
    public void dislikeComment(@PathVariable("postId") long postId, @PathVariable("commentId") long commentId, HttpSession session) throws BaseException {
        if(UserController.isLoggedIn(session)) {
            User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
            Comment comment = commentRepository.findById(commentId);
            if (comment.getUsersWhoLiked().contains(user)) {
                if(comment.getPostId() == postId) {
                    comment.getUsersWhoLiked().remove(user);
                    user.getLikedPosts().remove(comment);
                    userRepository.save(user);
                }
                else throw new PostExistException();
            }
            else throw new NotLikedPostException("Not liked this comment.");
        }
        else throw new NotLoggedException();
    }



}
