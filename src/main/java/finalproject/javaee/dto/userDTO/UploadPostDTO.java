package finalproject.javaee.dto.userDTO;

import finalproject.javaee.model.pojo.Media;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UploadPostDTO {

    private String description;
    private LocalDateTime date;
    private long categoriesId;
    private long userId;
    List<Media> media = new ArrayList<>(3); //up to 3 photos + 1 video

    public UploadPostDTO(String description, long categoriesId, List<Media> media, long userId){
        this.date = LocalDateTime.now();
        this.description = description;
        this.categoriesId = categoriesId;
        this.media = media;
        this.userId = userId;
    }


}
