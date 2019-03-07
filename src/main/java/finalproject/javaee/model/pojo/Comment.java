package finalproject.javaee.model.pojo;

import finalproject.javaee.dto.pojoDTO.DtoConvertible;
import finalproject.javaee.dto.pojoDTO.CommentDTO;
import finalproject.javaee.model.repository.UserRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "comments")
public class Comment implements DtoConvertible<CommentDTO> {

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

    @Transient
    @Autowired
    UserRepository userRepository;

    @Override
    public CommentDTO toDTO() {
        User user = userRepository.findById(this.userId);
        return new CommentDTO(user.getUsername(), user.getPhoto(), this.text);
    }
}
