package finalproject.javaee.dto.userDTO.editUserProfileDTO;

import finalproject.javaee.model.util.CryptWithMD5;
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

    public String getOldPassword() {
        return CryptWithMD5.crypt(oldPassword).trim();
    }
    public void setOldPassword(String oldPassword) {
        this.oldPassword = CryptWithMD5.crypt(oldPassword).trim();
    }

//    public String getNewPassword() {
//        return (newPassword).trim();
//    }
//    public String getVerifyNewPassword() {
//        return (verifyNewPassword).trim();
//    }
}
