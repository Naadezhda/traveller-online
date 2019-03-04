package finalproject.javaee.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "media")
public class Media {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    long id;
    String mediaUrl;
    long postId;

    public Media(long postId, String mediaUrl){
        this.postId = postId;
        this.mediaUrl = mediaUrl;

    }
}
