package finalproject.javaee.dto.userDTO;

import finalproject.javaee.dto.PostWithMediaInBytesDTO;
import finalproject.javaee.model.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ViewUserProfileDTO{

    private String username;
    private String photo;
    private long numberOfPost;
    private List<PostWithMediaInBytesDTO> posts;
    private long numberOfFollowing;
    private List<ViewUserRelationsDTO> following;
    private long numberOfFollower;
    private List<ViewUserRelationsDTO> follower;


    public ViewUserProfileDTO(String username, String photo){
        this.username = username;
        this.photo = photo;
        this.posts = new ArrayList<>();
        this.following= new ArrayList<>();
        this.follower = new ArrayList<>();
    }

    public ViewUserProfileDTO(String username, String photo, List<PostWithMediaInBytesDTO> posts) {
        this.username = username;
        this.photo = photo;
        this.posts = posts;
    }
}
