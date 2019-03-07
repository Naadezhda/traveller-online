package finalproject.javaee.controller;

import finalproject.javaee.dto.pojoDTO.MediaDTO;
import finalproject.javaee.dto.MessageDTO;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
@RestController
public class MediaController extends BaseController {

    @Autowired private UserRepository userRepository;
    @Autowired private UserController userController;
    @Autowired private MediaService mediaService;

    @PostMapping(value = "/images/users/photo")
    public MessageDTO uploadUserPhoto(@RequestBody MediaDTO dto, HttpSession session) throws Exception{
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return mediaService.addPhoto(user,dto);
    }

    @PostMapping(value = "/images/posts/{postId}/images")
    public MessageDTO uploadPostsImages(@RequestBody MediaDTO dto,
                                        @PathVariable("postId") long postId, HttpSession session) throws Exception{
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return mediaService.addImages(user,dto,postId);
    }

    @PostMapping(value = "/video/posts/{postId}")
    public MessageDTO uploadPostsVideo(@RequestBody MediaDTO dto,
                                       @PathVariable("postId") long postId, HttpSession session)throws Exception{
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        return mediaService.addVideo(user,dto,postId);
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
}
