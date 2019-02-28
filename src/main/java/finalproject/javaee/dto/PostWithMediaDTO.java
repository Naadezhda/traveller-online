package finalproject.javaee.dto;

import finalproject.javaee.model.pojo.Media;
import finalproject.javaee.model.pojo.Post;
import lombok.Getter;

import java.util.List;

@Getter
public class PostWithMediaDTO {

    Post post;
    List<Media> media;

    public PostWithMediaDTO(Post post, List<Media> media){
        this.post = post;
        this.media = media;
    }

}
