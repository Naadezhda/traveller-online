package finalproject.javaee.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post {

    private long id;
    private String descr;
    private LocalDateTime date;

    public Post(String descr){
        this.descr = descr;
        date = LocalDateTime.now();
    }
}
