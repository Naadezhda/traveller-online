package finalproject.javaee.dto.userDTO.editUserProfileDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ResetPasswordDTO {

    private String newPassword;
    private String verifyNewPassword;
}
