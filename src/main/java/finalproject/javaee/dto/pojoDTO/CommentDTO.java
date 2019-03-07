package finalproject.javaee.dto.pojoDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CommentDTO {

    private String username;
    private String photo;
    private String comment;

}
