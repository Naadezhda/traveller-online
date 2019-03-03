package finalproject.javaee.model.pojo;


import finalproject.javaee.model.util.CryptWithMD5;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements Comparable<User> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String username;
    @Size(min = 6)
    private String password;
    @Transient
    @Size(min = 6)
    private String verifyPassword;
    private String firstName;
    private String lastName;
    private String email;
    //TODO photo
    private String photo;
    private String gender;


    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "relations",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id"))
    private List<User> following;

    @ManyToMany(mappedBy = "following")
    private List<User> follower;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "likes_posts",
            joinColumns = @JoinColumn(name = "user_id" , referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"))
    private List<Post> likedPosts;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tags",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"))
    private List<Post> tagPost;

    public String getPassword() {
        return CryptWithMD5.crypt(password).trim();
    }

    public String getVerifyPassword() {
        return CryptWithMD5.crypt(verifyPassword).trim();
    }

    public void setPassword(String password) {
        this.password = CryptWithMD5.crypt(password).trim();
    }

    public void setVerifyPassword(String verifyPassword) {
        this.verifyPassword = CryptWithMD5.crypt(verifyPassword).trim();
    }

    @Override
    public int compareTo(User o) {
        return (int)(this.getId() - o.getId());
    }
}

