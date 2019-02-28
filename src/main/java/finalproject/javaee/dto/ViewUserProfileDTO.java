package finalproject.javaee.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ViewUserProfileDTO {

    private String username;
    private String photo;
    private List<PostWithMediaDTO> posts;

    public ViewUserProfileDTO(String username, String photo, List<PostWithMediaDTO> posts){
        this.username = username;
        this.photo = photo;
        this.posts = posts;
    }

}
