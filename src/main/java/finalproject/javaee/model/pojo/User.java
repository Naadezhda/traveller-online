package finalproject.javaee.model.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


    @NoArgsConstructor
    @Getter
    @Setter
    @Entity
    @Table(name = "users")
    public class User {

        @Id
        @GeneratedValue(strategy= GenerationType.IDENTITY)
        private long id;
        private String username;
        private String password;
        private String validatePassword;
        private String firstName;
        private String lastName;
        private String email;
        private String photo;
        private String gender;

        public User(long id, String username, String password, String validatePassword, String firstName, String lastName, String email, String gender) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.validatePassword = validatePassword;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.gender = gender;
        }

}
