package finalproject.javaee.dto;

import finalproject.javaee.model.pojo.Media;
import finalproject.javaee.model.pojo.Post;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PostWithMediaDTO {

    Post post;
    List<MediaInBytesDTO> media;

    public PostWithMediaDTO(Post post, List<MediaInBytesDTO> media){
        this.post = post;
        this.media = media;

    }

}
