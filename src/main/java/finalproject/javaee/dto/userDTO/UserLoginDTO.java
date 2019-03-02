package finalproject.javaee.dto.userDTO;

import finalproject.javaee.model.util.CryptWithMD5;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserLoginDTO {

    private String username;
    private String password;

    public String getPassword() {
        return CryptWithMD5.crypt(password);
    }

    public void setPassword(String password) {
        this.password = CryptWithMD5.crypt(password);
    }
}
