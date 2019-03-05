package finalproject.javaee.service;

import finalproject.javaee.controller.SearchController;
import finalproject.javaee.controller.UserController;
import finalproject.javaee.dto.*;
import finalproject.javaee.dto.userDTO.ViewUserProfileDTO;
import finalproject.javaee.dto.userDTO.ViewUserRelationsDTO;
import finalproject.javaee.dto.MediaDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    @Autowired
    UserController userController;
    @Autowired
    PostRepository postRepository;
    @Autowired
    MediaRepository mediaRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    SearchController searchController;
    @Autowired
    UserService userService;

    public List<PostWithUserAndMediaDTO> getAllPostsByCategory(User user, long categoryId) {
        List<ViewUserRelationsDTO> users = userService.getAllUserFollowing(user);
        List<PostWithUserAndMediaDTO> postsByFollowingWithMedia = new ArrayList<>();
        for (ViewUserRelationsDTO u : users) {
            List<Post> postsByFollowing = postRepository.findAllByUserIdAndCategoriesId(u.getId(), categoryId);
            List<PostWithUserAndMediaDTO> postsByUserWithMedia = getPostsByUserWithMedia(u, postsByFollowing);
            postsByFollowingWithMedia.addAll(postsByUserWithMedia);
        }
        return postsByFollowingWithMedia;
    }

    public ViewUserProfileDTO viewUserProfile(User user){
        return new ViewUserProfileDTO(user.getUsername(), user.getPhoto(),
                userService.getAllUserFollowing(user),
                userService.getAllUserFollowers(user),
                getAllUserPosts(user.getId()));

    }

    private boolean isValidNumberOfPhotos(List<String> photos) throws InvalidPostException{
        if(photos.size() > 3){
            throw new InvalidPostException("Cannot upload more than 3 photos.");
        }
        return true;
    }

    public AddPostWithMediaDTO addUserPost(User user, AddPostWithMediaDTO dto) throws InvalidPostException {
        Post p = new Post(user.getId(), dto.getDescription(), dto.getLocationId(), dto.getCategoriesId());
        postRepository.save(p);
        List<String> media = dto.getMediaURIs();
        isValidNumberOfPhotos(media);
        for (String s : media) {
            Media m = new Media(p.getId(), s);
            mediaRepository.save(m);
        }
        Media video = new Media(p.getId(), dto.getVideoURI());
        mediaRepository.save(video);
        return new AddPostWithMediaDTO(dto.getDescription(), dto.getLocationId(),
                dto.getCategoriesId(), media, dto.getVideoURI());
    }

    public List<PostWithMediaDTO> getAllUserPosts(Long id) {
        List<Post> posts = postRepository.findAllByUserId(id);
        List<PostWithMediaDTO> postWithMedia = new ArrayList<>();
        for (Post p : posts) {
            List<Media> postMedia = mediaRepository.findAllByPostId(p.getId());
            List<MediaDTO> postMediaDTO = listMediaToDTO(postMedia);
            postWithMedia.add(new PostWithMediaDTO(new PostDTO(p.getId(), p.getDescription(),
                    p.getLocationId(), p.getCategoriesId()), postMediaDTO));
        }
        return postWithMedia;
    }

    public List<PostWithUserAndMediaDTO> getAllPostsByFollowings(User user) {
        List<ViewUserRelationsDTO> users = userService.getAllUserFollowing(user);
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
            List<MediaDTO> mediadtos = new ArrayList();
            for (Media m : media) {
                mediadtos.add(new MediaDTO(m.getMediaUrl()));
            }
            postsByUser.add(new PostWithUserAndMediaDTO(u.getUsername(), u.getPhoto(), p.getDate(), new PostWithMediaDTO(dto, mediadtos), p.getUsersWhoLiked().size(), p.getUsersWhoLikedInDTO()));
        }
        return postsByUser;
    }

    public void likeUserPost(User user, long id) throws LikedPostException {
        Post post = postRepository.findById(id);
        if (!post.getUsersWhoLiked().contains(user)) {
            post.getUsersWhoLiked().add(user);
            user.getLikedPosts().add(post);
            userRepository.save(user);
        } else {
            throw new LikedPostException("Already liked this post.");
        }
    }

    public void dislikeUserPost(User user, long id) throws NotLikedPostException {
        Post post = postRepository.findById(id);
        if (post.getUsersWhoLiked().contains(user)) {
            post.getUsersWhoLiked().remove(user);
            user.getLikedPosts().remove(post);
            postRepository.save(post);
            userRepository.save(user);
        } else {
            throw new NotLikedPostException("Not liked this post.");
        }
    }

    public UserCommentDTO writeComment(User user, long id, String comment){
        Post post = postRepository.findById(id);
        Comment com = new Comment(user.getId(), post.getId(), comment);
        commentRepository.save(com);
        return new UserCommentDTO(user.getUsername(), user.getPhoto(), comment);
    }

    public UserCommentDTO deleteComment(User user, long id, long commentId) throws BaseException {
        Comment com = commentRepository.findById(commentId);
        if (commentRepository.findAllByPostId(id).contains(com)) {
            if (com.getUserId() == user.getId()) {
                commentRepository.delete(com);
                return new UserCommentDTO(user.getUsername(), user.getPhoto(), com.getText());
            }
            else throw new IllegalCommentException("Cannot delete other's comments");
        }
        else throw new IllegalCommentException("No such a comment");
    }

    public void likeComment(User user, long postId, long commentId) throws BaseException {
        Comment comment = commentRepository.findById(commentId);
        if (!comment.getUsersWhoLiked().contains(user)) {
            if (comment.getPostId() == postId) {
                comment.getUsersWhoLiked().add(user);
                user.getLikedComments().add(comment);
                userRepository.save(user);
            }
            else throw new InvalidPostException("This post doest't have such a comment");
        }
        else throw new LikedPostException("Already liked this comment.");
    }

    public void dislikeComment(User user, long postId, long commentId) throws BaseException {
        Comment comment = commentRepository.findById(commentId);
        if (comment.getUsersWhoLiked().contains(user)) {
            if (comment.getPostId() == postId) {
                comment.getUsersWhoLiked().remove(user);
                user.getLikedPosts().remove(comment);
                userRepository.save(user);
            }
            else throw new InvalidPostException("This post doest't have such a comment");
        }
        else throw new NotLikedPostException("Not liked this comment.");
    }

    public PostWithMediaDTO findPostById(long id) throws BaseException {
        if (!postRepository.existsById(id)) {
            throw new InvalidPostException("Does not exist post with such id");
        }
        Post post = postRepository.findById(id);
        List<Media> media = mediaRepository.findAllByPostId(post.getId());
        List<MediaDTO> mediaDtos = listMediaToDTO(media);
        return new PostWithMediaDTO(post.postToPostDTO(), mediaDtos);
    }

    public List<MediaDTO> listMediaToDTO(List<Media> media){
        List<MediaDTO> mediaDtos = new ArrayList<>();
        for (Media m : media) {
            mediaDtos.add(new MediaDTO(m.getMediaUrl()));
        }
        return mediaDtos;
    }

    public ViewUserProfileDTO viewProfile(long id) {
        User u = userRepository.findById(id);
        List<Post> posts = postRepository.findAllByUserId(u.getId());
        List<PostWithMediaDTO> postsWithMedia = new ArrayList<>();
        for (Post p : posts) {
            postsWithMedia.add(searchController.postToPostWithMediaDTO(p));
        }
        return new ViewUserProfileDTO(u.getUsername(), u.getPhoto(),
                userService.getAllUserFollowing(u),
                userService.getAllUserFollowers(u),
                postsWithMedia);
    }

}
