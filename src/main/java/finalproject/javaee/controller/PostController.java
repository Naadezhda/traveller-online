package finalproject.javaee.controller;

import finalproject.javaee.dto.MediaInBytesDTO;
import finalproject.javaee.dto.PostAPostWithMediaDTO;
import finalproject.javaee.dto.PostWithMediaInBytesDTO;
import finalproject.javaee.dto.PostWithMediaURL;
import finalproject.javaee.dto.PostWithUserAndMediaDTO;
import finalproject.javaee.dto.userDTO.ViewUserProfileDTO;
import finalproject.javaee.model.dao.PostDAO;
import finalproject.javaee.model.pojo.Media;
import finalproject.javaee.model.pojo.Post;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.MediaRepository;
import finalproject.javaee.model.repository.PostRepository;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.model.util.exceprions.BaseException;
import finalproject.javaee.model.util.exceprions.InvalidPostException;
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
            Optional<Post> post = postRepository.findById(id);
            if (post.isPresent()) {
                return post.get();
            } else {
                throw new InvalidPostException();
            }
        }
        throw new NotLoggedException();
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
            List<Post> posts = dao.getPostsByUser(user_id);
            User u = ur.findById(user_id);
            String username = u.getUsername();
            String photo = u.getPhoto();
            List<PostWithMediaInBytesDTO> postWithMedia = new ArrayList<>();
            for (Post p : posts) {
                List<Media> postMedia = mediaRepository.findAllByPostId(p.getId());
                List<MediaInBytesDTO> postMediaBytes = new ArrayList<>();
                for (Media m : postMedia) {
                    postMediaBytes.add(new MediaInBytesDTO(downloadImage(m.getMediaUrl())));
                }
                postWithMedia.add(new PostWithMediaInBytesDTO(p, postMediaBytes));
            }
            return new ViewUserProfileDTO(username, photo, postWithMedia);
        }
        throw new NotLoggedException();
    }

    public byte[] downloadImage(String mediaName) throws IOException {
        File file = new File(mediaName);
        return Files.readAllBytes(file.toPath());
    }

    @Autowired
    UserRepository userRepository;

    @PostMapping(value = "/users/addPost")
    public void addPost(@RequestBody PostAPostWithMediaDTO dto, HttpSession session) throws NotLoggedException{
        if(UserController.isLoggedIn(session)) {
            User user = ((User) (session.getAttribute("User")));
            Post p = new Post(user.getId(), dto.getDescription(), dto.getLocationId(), dto.getCategoriesId());
            postRepository.save(p);
            Media m = new Media(p.getId(), dto.getMediaUrl());
            mediaRepository.save(m);
        }
        else {
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
