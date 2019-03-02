package finalproject.javaee.dto;

import finalproject.javaee.dto.MediaInBytesDTO;
import finalproject.javaee.model.pojo.Media;
import finalproject.javaee.model.pojo.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class PostWithMediaURL {

    Post post;
    List<Media> media;

    public PostWithMediaURL(Post post, List<Media> media){
        this.post = post;
        this.media = media;

    }

}
