package finalproject.javaee.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class User {

    private long id;
    private String username;
    @JsonIgnore
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String photo;

    public User(long id, String username, String password, String firstName, String lastName, String email, String gender, String photo) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.photo = photo;
    }
}
