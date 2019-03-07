package finalproject.javaee.controller;

import finalproject.javaee.dto.pojoDTO.MediaDTO;
import finalproject.javaee.dto.MessageDTO;
import finalproject.javaee.model.pojo.Media;
import finalproject.javaee.model.pojo.Post;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.MediaRepository;
import finalproject.javaee.model.repository.PostRepository;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.model.util.exceptions.BaseException;
import finalproject.javaee.model.util.exceptions.postsExceptions.PostExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

@RestController
public class MediaController extends BaseController {

    public static final String MEDIA_DIR = "C:\\Users\\Vicky\\Desktop\\";

    @Autowired
    private MediaRepository mediaRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserController userController;
    @Autowired
    private PostRepository postRepository;

    @PostMapping(value = "/media")
    public MessageDTO uploadUserPhoto(@RequestBody MediaDTO dto, HttpSession session) throws Exception{
        User user = (User)session.getAttribute("loggedUser");
        String fileName = user.getId() + System.currentTimeMillis() + ".png";
        String mediaUri = readFile(dto.getMediaUri(),fileName);
        user.setPhoto(mediaUri);
        userRepository.save(user);
        return new MessageDTO("Upload successful.");
    }

    @GetMapping(value = "/media/{name}", produces = "image/png")
    public byte[] downloadImage(@PathVariable("name") String mediaName,HttpSession session) throws Exception {
        validateisLoggedIn(session);
        if(!mediaRepository.existsByMediaUrl(mediaName)) {
            throw new BaseException("Image does not exist.");
        }
        return download(mediaName);
    }

    @PostMapping(value = "/images/posts/{postId}/images")
    public MessageDTO uploadPostsImages(@RequestBody MediaDTO dto,@PathVariable("postId") long postId, HttpSession session) throws Exception {
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        if(!postRepository.existsById(postId)){
            throw new PostExistException();
        }
        Post post = postRepository.findById(postId);
        String fileName = user.getId() + System.currentTimeMillis() + ".png";
        String mediaUri = readFile(dto.getMediaUri(),fileName);
        dto.setMediaUri(mediaUri);
        Media media = new Media(post.getId(), dto.getMediaUri());
        mediaRepository.save(media);
        return new MessageDTO("Upload successful.");
    }

    @PostMapping(value = "/video/posts/{postId}")
    public MessageDTO uploadPostsVideo(@RequestBody MediaDTO dto,@PathVariable("postId") long postId, HttpSession session)throws Exception{
        User user = userRepository.findById(userController.getLoggedUserByIdSession(session));
        if(!postRepository.existsById(postId)){
            throw new PostExistException();
        }
        Post post = postRepository.findById(postId);

        String fileName = user.getId() + System.currentTimeMillis() + ".mp4";
        String videoUri = readFile(dto.getMediaUri(),fileName);
        dto.setMediaUri(videoUri);
        Media media = new Media(post.getId(), dto.getMediaUri());
        mediaRepository.save(media);
        return new MessageDTO("Upload video successful.");
    }

    @GetMapping(value = "/video/{name}", produces = "video/mp4")
    public byte[] downloadVideo(@PathVariable("name") String mediaName,HttpSession session) throws Exception {
        validateisLoggedIn(session);
        if(!mediaRepository.existsByMediaUrl(mediaName)){
            throw new BaseException("Video does not exist.");
        }
        return download(mediaName);
    }

    private String readFile(String media,String fileName) throws IOException {
        String base64 = media;
        byte[] bytes = Base64.getDecoder().decode(base64);
        File newImage = new File(MEDIA_DIR + fileName);
        FileOutputStream fos = new FileOutputStream(newImage);
        fos.write(bytes);
        fos.close();
        return fileName;
    }

    private byte[] download(String mediaName) throws IOException {
        File file = new File(MEDIA_DIR + mediaName);
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        fis.read(bytes);
        fis.close();
        return bytes;
    }

}
