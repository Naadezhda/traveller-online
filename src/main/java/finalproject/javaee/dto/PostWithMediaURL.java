package finalproject.javaee.dto;

import finalproject.javaee.model.pojo.Media;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class PostWithMediaURL {

    PostDTO post;
    List<Media> media;

    public PostWithMediaURL(PostDTO post, List<Media> media){
        this.post = post;
        this.media = media;

    }

}
