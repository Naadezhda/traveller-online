package finalproject.javaee.dto.userDTO;

import finalproject.javaee.dto.PostWithMediaInBytesDTO;
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
