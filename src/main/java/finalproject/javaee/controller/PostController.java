package finalproject.javaee.controller;

import finalproject.javaee.model.Post;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PostController {

    @GetMapping(value = "/posts/userId")
    public List<Post> getPostsByUserId(long id){

    }

}
