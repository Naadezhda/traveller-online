package finalproject.javaee.dto.userDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterInformationDTO {

    private long id;
    private String username;
    private String password;
    private String verifyPassword;
    private String firstName;
    private String lastName;
    private String email;
    private String photo;
    private String gender;
    private String secureCode;
    private boolean isCompleted;
    private boolean resetPassword;

}
