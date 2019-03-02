package finalproject.javaee.dto;

import finalproject.javaee.dto.PostWithMediaURL;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PostWithUserAndMediaDTO {

    private String username;
    private String photo;
    private List<PostWithMediaURL> posts;



}
