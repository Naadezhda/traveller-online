package finalproject.javaee.controller;

import finalproject.javaee.dto.MessageDTO;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.service.MediaService;
import finalproject.javaee.util.exceptions.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

@RestController
public class MediaController extends BaseController {

    @Autowired private UserRepository userRepository;
    @Autowired private UserController userController;
    @Autowired private MediaService mediaService;

    @PostMapping(value = "/images/users/photo")
    public MessageDTO uploadUserPhoto(@RequestPart(value = "image") MultipartFile image, HttpSession session) throws Exception{
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return mediaService.addPhoto(user,image);
    }

    @PostMapping(value = "/images/posts/{postId}/images")
    public MessageDTO uploadPostsImages(@RequestPart(value = "image") MultipartFile image,
                                        @PathVariable("postId") long postId, HttpSession session) throws Exception{
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return mediaService.addImages(user,image,postId);
    }

    @PostMapping(value = "/video/posts/{postId}")
    public MessageDTO uploadPostsVideo(@RequestPart(value = "video") MultipartFile video,
                                       @PathVariable("postId") long postId, HttpSession session)throws Exception{
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return mediaService.addVideo(user,video,postId);
    }

    @GetMapping(value = "/media/{name}", produces = "image/png")
    public byte[] downloadImage(@PathVariable("name") String mediaName,HttpSession session) throws Exception {
        validateisLoggedIn(session);
        return mediaService.imagesDownload(mediaName);
    }

    @GetMapping(value = "/video/{name}", produces = "video/mp4")
    public byte[] downloadVideo(@PathVariable("name") String mediaName,HttpSession session) throws Exception {
        validateisLoggedIn(session);
        return mediaService.videoDownload(mediaName);
    }

    @DeleteMapping(value = "/images/users/photo")
    public MessageDTO deleteUserPhoto(HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return mediaService.deletePhoto(user);
    }

    @DeleteMapping(value = "/images/posts/{postId}/media/{mediaId}")
    public MessageDTO deleteMedia(@PathVariable("postId") long postId,
                                       @PathVariable("mediaId") long mediaId, HttpSession session) throws BaseException {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return mediaService.deleteMedia(user,postId,mediaId);
    }
}
