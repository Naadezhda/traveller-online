package finalproject.javaee.dto.userDTO.editUserProfileDTO;

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

}
