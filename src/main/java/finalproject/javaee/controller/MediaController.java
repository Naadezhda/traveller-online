package finalproject.javaee.controller;

import finalproject.javaee.dto.MediaDTO;
import finalproject.javaee.model.pojo.Media;
import finalproject.javaee.model.pojo.Post;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.MediaRepository;
import finalproject.javaee.model.repository.PostRepository;
import finalproject.javaee.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Base64;

@RestController
public class MediaController extends BaseController {

    @Autowired private MediaRepository mediaRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserController userController;
    @Autowired private PostRepository postRepository;

    public static final String MEDIA_DIR = "C:\\Users\\Надежда\\Desktop\\Upload\\";

    @PostMapping(value = "/images/users/photo")
    public void uploadUserPhoto(@RequestBody MediaDTO dto, HttpSession session) throws Exception{
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        String base64 = dto.getMediaURI();
        byte[] bytes = Base64.getDecoder().decode(base64);
        String fileName = user.getId() + System.currentTimeMillis() + ".png";
        File newImage = new File(MEDIA_DIR + fileName);
        FileOutputStream fos = new FileOutputStream(newImage);
        fos.write(bytes);
        fos.close();
        user.setPhoto(fileName);
        userRepository.save(user);
    }
    @PostMapping(value = "/images/posts/images")
    public void uploadPostsImages(@RequestBody MediaDTO dto, HttpSession session) throws Exception{
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        Post post = postRepository.findByUserId(user.getId());
        String base64 = dto.getMediaURI();
        byte[] bytes = Base64.getDecoder().decode(base64);
        String fileName = user.getId() + System.currentTimeMillis() + ".png";
        File newImage = new File(MEDIA_DIR + fileName);
        FileOutputStream fos = new FileOutputStream(newImage);
        fos.write(bytes);
        fos.close();
        dto.setMediaURI(fileName);
        Media media = new Media(post.getId(), dto.getMediaURI());
        mediaRepository.save(media);
    }

    @GetMapping(value = "/media/{name}", produces = "image/png")
    public byte[] downloadImage(@PathVariable("name") String mediaName,HttpSession session) throws Exception {
        validateisLoggedIn(session);
        File file = new File(MEDIA_DIR + mediaName);
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = new byte[(int)file.length()];
        fis.read(bytes);
        fis.close();
        return bytes;
    }

}
