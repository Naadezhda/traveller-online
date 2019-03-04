package finalproject.javaee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostDTO {

    private long id;
    private String description;
    private long locationId;
    private long categoriesId;
    private LocalDateTime date;
}
