package finalproject.javaee.dto.pojoDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PostDTO {

    private long id;
    private String description;
    private LocationDTO location;
    private long categoriesId;

}
