package finalproject.javaee.dto;

import finalproject.javaee.model.pojo.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostWithMediaInBytesDTO{

    private Post post;
    private List<MediaInBytesDTO> media;

    public PostWithMediaInBytesDTO(Post post, List<MediaInBytesDTO> media){
        this.post = post;
        this.media = media;

    }

}
