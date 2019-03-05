package finalproject.javaee.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostWithMediaDTO {

    private PostDTO post;
    private List<MediaDTO> media;

    public PostWithMediaDTO(PostDTO post, List<MediaDTO> media){
        this.post = post;
        this.media = media;
    }

}
