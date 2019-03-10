package finalproject.javaee.dto.userDTO;

import finalproject.javaee.dto.PostWithMediaDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class ViewUserProfileDTO{

    private String username;
    private String photo;
    private long numberOfFollowings;
    private List<ViewUserRelationsDTO> followings;
    private long numberOfFollowers;
    private List<ViewUserRelationsDTO> followers;
    private long numberOfPost;
    private List<PostWithMediaDTO> posts;

    public ViewUserProfileDTO(String username, String photo,
                              List<ViewUserRelationsDTO> followings, List<ViewUserRelationsDTO> followers,
                              List<PostWithMediaDTO> posts) {
        this.username = username;
        this.photo = photo;
        this.posts = posts;
        this.numberOfPost = posts.size();
        this.followings = followings;
        this.numberOfFollowings = followings.size();
        this.followers = followers;
        this.numberOfFollowers = followers.size();
    }
}
