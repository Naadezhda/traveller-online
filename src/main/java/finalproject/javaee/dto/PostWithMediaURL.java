package finalproject.javaee.dto;

import finalproject.javaee.model.pojo.Media;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PostWithMediaURL {

    PostDTO post;
    List<Media> media;//up to 3 media

}
