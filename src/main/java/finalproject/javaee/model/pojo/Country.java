package finalproject.javaee.model.pojo;

import finalproject.javaee.dto.pojoDTO.CountryDTO;
import finalproject.javaee.dto.pojoDTO.DtoConvertible;
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
public class Country implements DtoConvertible<CountryDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String countryName;
    private long regionId;

    @Override
    public CountryDTO toDTO() {
        return new CountryDTO(this.id, this.countryName);
    }
}
