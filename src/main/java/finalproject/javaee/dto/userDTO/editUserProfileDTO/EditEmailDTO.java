package finalproject.javaee.dto.userDTO.editUserProfileDTO;

import finalproject.javaee.model.util.CryptWithMD5;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Transient;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class EditEmailDTO {

    @Transient
    private String password;
    private String newEmail;

    public String getPassword() {
        return CryptWithMD5.crypt(password).trim();
    }

    public void setPassword(String password) {
        this.password = CryptWithMD5.crypt(password).trim();
    }
}
