package finalproject.javaee.dto.userDTO;

import finalproject.javaee.model.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserLoginDTO {

//    private long id;
    private String username;
    private String password;
}
