package finalproject.javaee.model.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    private long userId;
    private long postId;
    private String text;
    private LocalDateTime date;

    @ManyToMany(fetch = FetchType.EAGER,
            mappedBy = "likedComments")
    private Set<User> usersWhoLiked;

    public Comment(long userId, long postId, String text){
        this.userId = userId;
        this.postId = postId;
        this.text = text;
        this.date = LocalDateTime.now();
    }
}
