package finalproject.javaee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PostAPostWithMediaDTO {

    String description;
    long locationId;
    long categoriesId;
    String mediaUrl;


}
