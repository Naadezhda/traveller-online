package finalproject.javaee.controller;

import finalproject.javaee.dto.*;
import finalproject.javaee.dto.userDTO.ViewUserProfileDTO;
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
import finalproject.javaee.model.util.exceprions.BaseException;
import finalproject.javaee.model.util.exceprions.postsExceptions.IllegalCommentException;
import finalproject.javaee.model.util.exceprions.postsExceptions.InvalidPostException;
import finalproject.javaee.model.util.exceprions.postsExceptions.LikedPostException;
import finalproject.javaee.model.util.exceprions.postsExceptions.NotLikedPostException;
import finalproject.javaee.model.util.exceprions.usersExceptions.NotLoggedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class PostController extends BaseController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    UserController userController;

    @Autowired
    private PostDAO dao;

    @GetMapping(value = "/posts/users/{userId}")
    public List<Post> getPostsByUserId(@PathVariable("userId") long id, HttpSession session) throws NotLoggedException {
        if(UserController.isLoggedIn(session)) {
            List<Post> posts = dao.getPostsByUser(id);
            return posts;
        }
        throw new NotLoggedException();
    }

    @GetMapping(value = "/posts/{id}")
    public Post getPostByPostId(@PathVariable("id") long id, HttpSession session) throws BaseException {
        if(UserController.isLoggedIn(session)) {
           return findPostById(id);
        }
        throw new NotLoggedException();
    }

    @Autowired
    CommentRepository commentRepository;

    @PostMapping(value = "/posts/{id}/comment")
    public UserCommentDTO postComment(@PathVariable("id") long id, @RequestBody String comment, HttpSession session) throws BaseException{
        if(UserController.isLoggedIn(session)) {
            User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
            Post post = findPostById(id);
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



    public Post findPostById(long id) throws BaseException {
//        Optional<Post> post = postRepository.findById(id);
//        if (post.isPresent()) {
//            return post.get();
//        } else {
//            throw new InvalidPostException();
//        }
        //TODO ne raboteshe po gorniq nachin
        Post post = postRepository.findById(id);
        if(!postRepository.existsById(id)){
            throw new InvalidPostException();
        }
        return post;
    }


    @GetMapping(value = "/newsfeed/categories/{category}")
    public List<PostWithUserAndMediaDTO> getPostsByCategory(@PathVariable("category") int categoryId, HttpSession session) throws NotLoggedException{
        if(UserController.isLoggedIn(session)) {
            User user = ((User) (session.getAttribute("User")));
            List<User> users = userRepository.findAllByFollowerId(user.getId());
            List<PostWithUserAndMediaDTO> postsByFollowingWithMedia = new ArrayList<>();
            for (User u : users) {
                List<Post> postsByFollowing = postRepository.findAllByUserIdAndCategoriesId(u.getId(), categoryId);
                if(postsByFollowing.size() > 0) {
                    PostWithUserAndMediaDTO post = getPostsByUserWithMedia(u, postsByFollowing);
                    postsByFollowingWithMedia.add(post);
                }
            }
            return postsByFollowingWithMedia;
        }
        throw new NotLoggedException();
    }

    @Autowired
    UserRepository ur;

    @Autowired
    MediaRepository mediaRepository;

    @GetMapping(value = "/profile/users/{user}")
    public ViewUserProfileDTO getUserProfile(@PathVariable("user") long user_id, HttpSession session) throws NotLoggedException, IOException{
        if(UserController.isLoggedIn(session)) {
            User u = ur.findById(user_id);
            String username = u.getUsername();
            String photo = u.getPhoto();
            return new ViewUserProfileDTO(username, photo,getAllUserPosts(user_id).size(), getAllUserPosts(user_id),
                    userController.getAllUserFollowing(u).size(),userController.getAllUserFollowing(u),
                    userController.getAllUserFollowers(u).size(),userController.getAllUserFollowers(u));
        }
        throw new NotLoggedException();
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



    @Autowired
    UserRepository userRepository;

    @PostMapping(value = "/users/addPost")
    public void addPost(@RequestBody PostAPostWithMediaDTO dto, HttpSession session) throws NotLoggedException {
        if (UserController.isLoggedIn(session)) {
            User user = ((User) (session.getAttribute("User")));
            Post p = new Post(user.getId(), dto.getDescription(), dto.getLocationId(), dto.getCategoriesId());
            postRepository.save(p);
            Media m = new Media(p.getId(), dto.getMediaUrl());
            mediaRepository.save(m);
        } else {
            throw new NotLoggedException();
        }
    }

    @GetMapping(value = "/newsfeed")
    public List<PostWithUserAndMediaDTO> getAll(HttpSession session) throws NotLoggedException{
        if(UserController.isLoggedIn(session)) {
            User user = ((User) (session.getAttribute("User")));
            return getAllPostsByFollowings(user);
        }
        throw new NotLoggedException();
    }


    public List<PostWithUserAndMediaDTO> getAllPostsByFollowings(User user) {
        List<User> users = userRepository.findAllByFollowerId(user.getId());
        List<PostWithUserAndMediaDTO> allPostsByFollowings = new ArrayList<>();
        for (User u : users) {
            List<Post> postsByFollowing = postRepository.findAllByUserId(u.getId());
            PostWithUserAndMediaDTO post = getPostsByUserWithMedia(u, postsByFollowing);
            allPostsByFollowings.add(post);
        }
        return allPostsByFollowings;
    }

    public PostWithUserAndMediaDTO getPostsByUserWithMedia(User u, List<Post> postsByFollowing) {
        List<Media> media;
        List<PostWithMediaURL> postWithMedia = new ArrayList<>();
        for (Post p : postsByFollowing) {
            media = mediaRepository.findAllByPostId(p.getId());
            postWithMedia.add(new PostWithMediaURL(p, media));
        }
        return new PostWithUserAndMediaDTO(u.getUsername(), u.getPhoto(), postWithMedia);
    }



}
