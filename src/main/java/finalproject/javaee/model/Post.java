package finalproject.javaee.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
public class Post {

    private long id;
    private String desc;
    private Date date;

    public Post(long id, String desc, Date date) {
        this.id = id;
        this.desc = desc;
        this.date = date;
    }
}
