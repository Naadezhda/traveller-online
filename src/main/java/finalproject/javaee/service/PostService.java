package finalproject.javaee.service;

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
import finalproject.javaee.util.exceptions.usersExceptions.ExistException;
import finalproject.javaee.util.exceptions.usersExceptions.NotLoggedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    @Autowired private PostRepository postRepository;
    @Autowired private MediaRepository mediaRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;
    @Autowired private LocationRepository locationRepository;
    @Autowired private CountryRepository countryRepository;
    @Autowired private CategoryRepository categoryRepository;

    public List<PostWithUserAndMediaDTO> getAllPostsByCategory(User user, long categoryId) throws BaseException {
        validateIfCategoryExist(categoryId);
        List<ViewUserRelationsDTO> users = userService.getAllUserFollowing(user);
        List<PostWithUserAndMediaDTO> postsByFollowingWithMedia = new ArrayList<>();
        for (ViewUserRelationsDTO u : users) {
            List<Post> postsByFollowing = postRepository.findAllByUserIdAndCategoriesId(u.getId(), categoryId);
            List<PostWithUserAndMediaDTO> postsByUserWithMedia = getPostsByUserWithMedia(u, postsByFollowing);
            postsByFollowingWithMedia.addAll(postsByUserWithMedia);
        }
        return postsByFollowingWithMedia;
    }

    public PostWithMediaDTO addUserPost(User user, AddPostWithMediaDTO dto) throws BaseException {
        validateIfLocationExist(dto.getLocationId());
        validateIfCategoryExist(dto.getCategoriesId());
        Post p = new Post(user.getId(), dto.getDescription(), dto.getLocationId(), dto.getCategoriesId());
        postRepository.save(p);
        return new PostWithMediaDTO(postToPostDTO(p), new ArrayList<>());
    }

    public MessageDTO deleteUserPost(User user, long postId) throws BaseException {
        validateIfPostExist(postId);
        Post p = postRepository.findById(postId);
        if(user.getId() != userRepository.findById(p.getUserId()).getId()){
            throw new NotLoggedException("Cannot delete others' posts.");
        }
        postRepository.delete(p);
        return new MessageDTO("Post deleted successfully.");
    }

    public List<PostWithMediaDTO> getAllUserPosts(Long id) throws BaseException {
        userService.validateIfUserExist(id);
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
            PostDTO dto = new PostDTO(p.getId(), p.getDescription(), locationToLocationDTO(location), p.getCategoriesId());
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

    private CountryDTO countryToCountryDTO(Country country){
        return new CountryDTO(country.getId(), country.getCountryName());
    }

    private LocationDTO locationToLocationDTO(Location location){
        Country country = countryRepository.findById(location.getCountryId());
        return new LocationDTO(location.getId(), location.getCity(), countryToCountryDTO(country), location.getLongitude(),
                location.getLatitude());
    }

    public PostDTO postToPostDTO(Post post){
        Location location = locationRepository.findById(post.getLocationId());
        return new PostDTO(post.getId(), post.getDescription(), locationToLocationDTO(location), post.getCategoriesId());
    }

    public MessageDTO likeUserPost(User user, long id) throws BaseException {
        validateIfPostExist(id);
        Post post = postRepository.findById(id);
        if (!post.getUsersWhoLiked().contains(user)) {
            post.getUsersWhoLiked().add(user);
            user.getLikedPosts().add(post);
            userRepository.save(user);
        } else {
            throw new BaseException("Already liked this post.");
        }
        return new MessageDTO(user.getUsername() + " liked " +
                userRepository.findById(post.getUserId()).getUsername() + "'s post.");
    }

    public MessageDTO dislikeUserPost(User user, long id) throws BaseException {
        validateIfPostExist(id);
        Post post = postRepository.findById(id);
        if (post.getUsersWhoLiked().contains(user)) {
            post.getUsersWhoLiked().remove(user);
            user.getLikedPosts().remove(post);
            userRepository.save(user);
        } else {
            throw new BaseException("Not liked this post.");
        }
        return new MessageDTO(user.getUsername() + " disliked " +
                userRepository.findById(post.getUserId()).getUsername() + "'s post.");
    }

    public PostWithMediaDTO findPostById(long id) throws BaseException {
        validateIfPostExist(id);
        Post post = postRepository.findById(id);
        List<Media> media = mediaRepository.findAllByPostId(post.getId());
        List<MediaDTO> mediaDtos = listMediaToDTO(media);
        return new PostWithMediaDTO(postToPostDTO(post), mediaDtos);
    }

    public List<MediaDTO> listMediaToDTO(List<Media> media){
        List<MediaDTO> mediaDtos = new ArrayList<>();
        for (Media m : media) {
            mediaDtos.add(new MediaDTO(m.getMediaUrl()));
        }
        return mediaDtos;
    }

    public ViewUserProfileDTO viewUserProfile(long userId) throws BaseException{
        userService.validateIfUserExist(userId);
        User user = userRepository.findById(userId);
        return new ViewUserProfileDTO(user.getUsername(), user.getPhoto(),
                userService.getAllUserFollowing(user),
                userService.getAllUserFollowers(user),
                getAllUserPosts(user.getId()));
    }

    public List<PostWithUserAndMediaDTO> getTaggedPosts(long id) throws BaseException {
        userService.validateIfUserExist(id);
        List<Post> taggedPosts = postRepository.findAllByTagUserId(id);
        List<PostWithUserAndMediaDTO> postsWithMedia = new ArrayList<>();
        for (Post p : taggedPosts) {
            List<Media> media = mediaRepository.findAllByPostId(p.getId());
            User u = userRepository.findById(p.getUserId());
            PostWithMediaDTO postDto = new PostWithMediaDTO(postToPostDTO(p), listMediaToDTO(media));
            postsWithMedia.add(new PostWithUserAndMediaDTO(u.getUsername(), u.getPhoto(), p.getDate(),
                    postDto, p.getUsersWhoLiked().size(), p.getUsersWhoLikedInDTO()));
        }
        return postsWithMedia;
    }

    protected void validateIfPostExist(long postId)throws BaseException {
        if(!postRepository.existsById(postId)) {
            throw new ExistException("Does not exist post with such id.");
        }
    }

    private void  validateIfLocationExist(long locationId) throws BaseException{
        if(!locationRepository.existsById(locationId)){
            throw new ExistException("Location does not exist.");
        }
    }

    private void  validateIfCategoryExist(long categoryId) throws BaseException{
        if(!categoryRepository.existsById(categoryId)){
            throw new ExistException("Category does not exist.");
        }
    }

}
