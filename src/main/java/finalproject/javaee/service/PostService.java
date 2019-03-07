package finalproject.javaee.service;

import finalproject.javaee.controller.SearchController;
import finalproject.javaee.dto.*;
import finalproject.javaee.dto.pojoDTO.CountryDTO;
import finalproject.javaee.dto.pojoDTO.LocationDTO;
import finalproject.javaee.dto.pojoDTO.PostDTO;
import finalproject.javaee.dto.userDTO.ViewUserProfileDTO;
import finalproject.javaee.dto.userDTO.ViewUserRelationsDTO;
import finalproject.javaee.dto.pojoDTO.MediaDTO;
import finalproject.javaee.model.pojo.*;
import finalproject.javaee.model.repository.*;
import finalproject.javaee.util.exceptions.BaseException;
import finalproject.javaee.util.exceptions.postsExceptions.*;
import finalproject.javaee.util.exceptions.usersExceptions.ExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackOn = BaseException.class)
public class PostService {


    @Autowired private PostRepository postRepository;
    @Autowired private MediaRepository mediaRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private SearchController searchController;
    @Autowired private UserService userService;
    @Autowired private LocationRepository locationRepository;
    @Autowired private CountryRepository countryRepository;

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

    public ViewUserProfileDTO viewUserProfile(long userId) throws BaseException{
        if(!userRepository.existsById(userId)) {
            throw new ExistException("There is no user with such id!");
        }
        else{
            System.out.println("User with id " + userId);
        }
        User user = userRepository.findById(userId);
        return new ViewUserProfileDTO(user.getUsername(), user.getPhoto(),
                userService.getAllUserFollowing(user),
                userService.getAllUserFollowers(user),
                getAllUserPosts(user.getId()));

    }

    public AddPostWithMediaDTO addUserPost(User user, AddPostWithMediaDTO dto)  {
        Post p = new Post(user.getId(), dto.getDescription(), dto.getLocationId(), dto.getCategoriesId());
        postRepository.save(p);
        return new AddPostWithMediaDTO(dto.getDescription(), dto.getLocationId(),
                dto.getCategoriesId());
    }

    public List<PostWithMediaDTO> getAllUserPosts(Long id) throws BaseException {
        if(!userRepository.existsById(id)) {
            throw new ExistException("There is no user with such id!");
        }
        List<Post> posts = postRepository.findAllByUserId(id);
        List<PostWithMediaDTO> postWithMedia = new ArrayList<>();
        for (Post p : posts) {
            Location location = locationRepository.findById(p.getLocationId());
            Country country = countryRepository.findById(location.getCountryId());
            LocationDTO locDTO = new LocationDTO(location.getId(), location.getCity(),
                    new CountryDTO(country.getId(), country.getCountryName()),
                    location.getLongitude(), location.getLatitude());
            List<Media> postMedia = mediaRepository.findAllByPostId(p.getId());
            List<MediaDTO> postMediaDTO = listMediaToDTO(postMedia);
            postWithMedia.add(new PostWithMediaDTO(new PostDTO(p.getId(), p.getDescription(),
                    locDTO, p.getCategoriesId()), postMediaDTO));
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
            Location location = locationRepository.findById(p.getLocationId());
            PostDTO dto = new PostDTO(p.getId(), p.getDescription(), location.toDTO(), p.getCategoriesId());
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

    public MessageDTO likeUserPost(User user, long id) throws BaseException {
        if (!postRepository.existsById(id)) {
            throw new InvalidPostException("Does not exist post with such id");
        }
        Post post = postRepository.findById(id);
        if (!post.getUsersWhoLiked().contains(user)) {
            post.getUsersWhoLiked().add(user);
            user.getLikedPosts().add(post);
            userRepository.save(user);
        } else {
            throw new LikedPostException("Already liked this post.");
        }
        return new MessageDTO(user.getUsername() + " liked " +
                userRepository.findById(post.getUserId()).getUsername() + "'s post.");
    }

    public MessageDTO dislikeUserPost(User user, long id) throws BaseException {
        if (!postRepository.existsById(id)) {
            throw new InvalidPostException("Does not exist post with such id");
        }
        Post post = postRepository.findById(id);
        if (post.getUsersWhoLiked().contains(user)) {
            post.getUsersWhoLiked().remove(user);
            user.getLikedPosts().remove(post);
            postRepository.save(post);
            userRepository.save(user);
        } else {
            throw new NotLikedPostException("Not liked this post.");
        }
        return new MessageDTO(user.getUsername() + " disliked " +
                userRepository.findById(post.getUserId()).getUsername() + "'s post.");
    }

    public PostWithMediaDTO findPostById(long id) throws BaseException {
        if (!postRepository.existsById(id)) {
            throw new InvalidPostException("Does not exist post with such id");
        }
        Post post = postRepository.findById(id);
        List<Media> media = mediaRepository.findAllByPostId(post.getId());
        List<MediaDTO> mediaDtos = listMediaToDTO(media);
        return new PostWithMediaDTO(post.toDTO(), mediaDtos);
    }

    public List<MediaDTO> listMediaToDTO(List<Media> media){
        List<MediaDTO> mediaDtos = new ArrayList<>();
        for (Media m : media) {
            mediaDtos.add(new MediaDTO(m.getMediaUrl()));
        }
        return mediaDtos;
    }

    public ViewUserProfileDTO viewProfile(long id) throws BaseException {
        if(!userRepository.existsById(id)) {
            throw new ExistException("There is no user with such id!");
        }
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

    protected void validateIfPostExist(long postId)throws BaseException {
        if(!postRepository.existsById(postId)) {
            throw new ExistException("Post doesn't exist");
        }
    }

}
