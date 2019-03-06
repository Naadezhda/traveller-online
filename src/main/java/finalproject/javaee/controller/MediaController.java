package finalproject.javaee.controller;

import finalproject.javaee.dto.MediaDTO;
import finalproject.javaee.model.pojo.User;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

@RestController
public class MediaController extends BaseController {

    public static final String MEDIA_DIR = "C:\\Users\\Vicky\\Desktop\\";

    @PostMapping(value = "/media")
    public void uploadImage(@RequestBody MediaDTO dto, HttpSession session) throws Exception{ //TODO
        User user = (User)session.getAttribute("loggedUser");
        String base64 = dto.getMediaURI();
        byte[] bytes = Base64.getDecoder().decode(base64);
        System.out.println(base64);
        //String fileName = user.getId() + System.currentTimeMillis() + ".png";
        String fileName = "media.png";
        File newImage = new File(fileName);
        FileOutputStream fos = new FileOutputStream(newImage);
        fos.write(bytes);
    }

    @GetMapping(value = "/media/{name}", produces = "image/png")
    public byte[] downloadImage(@PathVariable("name") String mediaName) throws IOException {
        File file = new File(mediaName);
        return Files.readAllBytes(file.toPath());
    }
}
