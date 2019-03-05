package finalproject.javaee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AddPostWithMediaDTO {

    String description;
    long locationId;
    long categoriesId;
    List<String> mediaURIs;
    String videoURI;

}
