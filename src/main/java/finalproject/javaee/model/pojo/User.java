package finalproject.javaee.model.pojo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

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
    private String photo;
    private String gender;
    private String secureCode;
    private boolean isCompleted;
    private boolean resetPassword;

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
    private Set<Post> likedPosts;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tags",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"))
    private Set<Post> tagPost;

    @ManyToMany(fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinTable(name = "likes_comments",
            joinColumns = @JoinColumn(name = "user_id" , referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id", referencedColumnName = "id"))
    private Set<Comment> likedComments;

    public User(String username, String password, String firstName, String lastName, String email,
                                  String gender, String secureCode) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.photo = "default.png";
        this.gender = gender;
        this.secureCode = secureCode;
        this.isCompleted=false;
        this.resetPassword = false;
    }

    @Override
    public int compareTo(User o) {
        return (int)(this.getId() - o.getId());
    }


}

