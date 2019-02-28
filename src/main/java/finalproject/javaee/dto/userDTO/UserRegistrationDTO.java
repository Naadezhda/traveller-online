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
public class UserRegistrationDTO {

    public User user;
    private String username;
    private String password;
    private String verifyPassword;
    private String firstName;
    private String lastName;
    private String email;
    private String photo;
    private String gender;
}
