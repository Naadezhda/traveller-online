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
@Table(name = "countries")
public class Country  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String countryName;
    private long regionId;

}
