package finalproject.javaee.model.pojo;
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
    private String descriprion;
    private LocalDateTime date;

    public Post(String descriprion) {
        this.descriprion = descriprion;
        this.date = LocalDateTime.now();
    }
}