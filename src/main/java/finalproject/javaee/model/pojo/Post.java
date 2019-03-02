package finalproject.javaee.model.pojo;
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
    private long userId;
    private String description;
    private long locationId;
    private long categoriesId;
    private LocalDateTime date;


    public Post(String description) {
        this.description = description;
        this.date = LocalDateTime.now();
    }

    public Post(long userId, String description, long locationId,long categoriesId) {
        this(description);
        this.userId = userId;
        this.description = description;
        this.locationId = locationId;
        this.categoriesId = categoriesId;
    }


}