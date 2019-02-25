package finalproject.javaee.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post {

    private long id;
    private String descr;
    private LocalDateTime date;

    public Post(String descr) {
        this.descr = descr;
        date = LocalDateTime.now();
    }
}