package finalproject.javaee.dto;

import finalproject.javaee.dto.userDTO.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PostWithUserAndMediaDTO {

    private String username;
    private String photo;
    private LocalDateTime date;
    private PostWithMediaDTO postWithMedia;
    int numOfLikes;
    Set<UserDTO> likes;

}
