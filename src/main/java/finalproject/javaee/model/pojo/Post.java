package finalproject.javaee.model.pojo;
import finalproject.javaee.dto.pojoDTO.DtoConvertible;
import finalproject.javaee.dto.pojoDTO.PostDTO;
import finalproject.javaee.dto.userDTO.UserDTO;
import finalproject.javaee.model.repository.LocationRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post implements DtoConvertible<PostDTO> {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    private long userId;
    private String description;
    private long locationId;
    private long categoriesId;
    private LocalDateTime date;

    @ManyToMany(fetch = FetchType.EAGER,
            mappedBy = "likedPosts")
    private Set<User> usersWhoLiked;

    @ManyToMany(fetch = FetchType.EAGER,
            mappedBy = "tagPost")
    private Set<User> tagUser;

    public Post(String description) {
        this.description = description;
        //this.date = LocalDateTime.now();
        date = LocalDateTime.now();
    }

    public Post(long userId, String description, long locationId,long categoriesId) {
        this(description);
        this.userId = userId;
        this.description = description;
        this.locationId = locationId;
        this.categoriesId = categoriesId;
    }

    public Set<UserDTO> getUsersWhoLikedInDTO(){
        Set<UserDTO> usersWhoLikedInDTO = new HashSet<>();
        for (User u : usersWhoLiked) {
            usersWhoLikedInDTO.add(new UserDTO(u.getUsername(), u.getPhoto()));
        }
        return  usersWhoLikedInDTO;
    }

    @Transient
    @Autowired
    LocationRepository locationRepository;

    @Override
    public PostDTO toDTO() {
        Location location = locationRepository.findById(this.locationId);
        return new PostDTO(this.id, this.description, location.toDTO(), this.categoriesId);
    }

}