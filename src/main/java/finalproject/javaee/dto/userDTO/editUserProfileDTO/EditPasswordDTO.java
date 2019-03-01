package finalproject.javaee.dto.userDTO.editUserProfileDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EditPasswordDTO {

    private String oldPassword;
    private String newPassword;
    private String verifyNewPassword;
}
