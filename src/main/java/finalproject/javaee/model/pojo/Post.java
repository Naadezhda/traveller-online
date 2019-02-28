package finalproject.javaee.model.pojo;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    private String description;
    private LocalDateTime date;
    private int categoriesId;
    private long userId;

    public Post(String description) {
        this.description = description;
        this.date = LocalDateTime.now();
    }

    public Post(long id, String description) {
        this(description);
        this.id = id;
    }


}