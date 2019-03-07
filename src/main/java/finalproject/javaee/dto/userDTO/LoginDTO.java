package finalproject.javaee.dto.userDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginDTO {

    private String username;
    private String password;

//    public String getPassword() {
//        return Crypt.hashPassword(password);
//    }
//
//    public void setPassword(String password) {
//        this.password = Crypt.hashPassword(password);
//    }
}
