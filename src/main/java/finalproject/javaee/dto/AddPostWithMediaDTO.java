package finalproject.javaee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AddPostWithMediaDTO {

    private String description;
    private long locationId;
    private long categoriesId;

}
