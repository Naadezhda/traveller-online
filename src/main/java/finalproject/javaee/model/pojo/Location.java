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
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    private String city;
    private long countryId;
    private double longitude;
    private double latitude;

}
