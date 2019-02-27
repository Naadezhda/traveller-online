package finalproject.javaee.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Entity
    @Table(name = "users")
    public class User {

        @Id
        @GeneratedValue(strategy= GenerationType.IDENTITY)
        private long id;
        @Column
        private String username;
        @Size(min=6)
        private String password;
        @Transient
        @Size(min=6)
        private String verifyPassword;
        private String firstName;
        private String lastName;
        private String email;
        private String photo;
        private String gender;

}
