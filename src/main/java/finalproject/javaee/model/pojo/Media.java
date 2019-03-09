package finalproject.javaee.model.pojo;

import finalproject.javaee.dto.pojoDTO.DtoConvertible;
import finalproject.javaee.dto.pojoDTO.MediaDTO;
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
public class Media implements DtoConvertible<MediaDTO> {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    private String mediaUrl;
    private long postId;

    public Media(long postId, String mediaUrl){
        this.postId = postId;
        this.mediaUrl = mediaUrl;
    }

    @Override
    public MediaDTO toDTO() {
        return new MediaDTO(this.getMediaUrl());
    }
}
