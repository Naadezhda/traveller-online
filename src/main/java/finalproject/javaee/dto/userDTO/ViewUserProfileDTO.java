package finalproject.javaee.dto.userDTO;

import finalproject.javaee.dto.PostWithMediaDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class ViewUserProfileDTO{

    private String username;
    private String photo;
    private List<PostWithMediaDTO> posts;

    public ViewUserProfileDTO(String username, String photo){
        this.username = username;
        this.photo = photo;
        this.posts = new ArrayList<>();
    }

    public ViewUserProfileDTO(String username, String photo, List<PostWithMediaDTO> posts) {
        this.username = username;
        this.photo = photo;
        this.posts = posts;
    }
}
