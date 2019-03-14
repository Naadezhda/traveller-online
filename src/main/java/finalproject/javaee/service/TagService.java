package finalproject.javaee.service;

import finalproject.javaee.dto.MessageDTO;
import finalproject.javaee.model.pojo.Post;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.PostRepository;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.util.exceptions.BaseException;
import finalproject.javaee.util.exceptions.usersExceptions.UserRelationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TagService {

    @Autowired private UserRepository userRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private UserService userService;
    @Autowired private PostService postService;

    public MessageDTO tagUser(long userId, long postId) throws BaseException {
        User user = userRepository.findById(userId);
        Post post = postRepository.findById(postId);
        postService.validateIfPostExist(postId);
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

    public MessageDTO deleteTagUser(long userId, long postId) throws BaseException {
        User user = userRepository.findById(userId);
        Post post = postRepository.findById(postId);
        postService.validateIfPostExist(postId);
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

}
