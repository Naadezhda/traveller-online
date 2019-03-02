package finalproject.javaee.dto.userDTO.editUserProfileDTO;

import finalproject.javaee.model.util.CryptWithMD5;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeleteUserProfileDTO {

    private String confirmPassword;

    public String getConfirmPassword() {
        return CryptWithMD5.crypt(confirmPassword).trim();
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = CryptWithMD5.crypt(confirmPassword).trim();
    }
}
