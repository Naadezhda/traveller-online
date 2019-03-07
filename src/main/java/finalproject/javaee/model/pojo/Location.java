package finalproject.javaee.model.pojo;

import finalproject.javaee.dto.pojoDTO.DtoConvertible;
import finalproject.javaee.dto.pojoDTO.LocationDTO;
import finalproject.javaee.model.repository.CountryRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "locations")
public class Location implements DtoConvertible<LocationDTO> {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    private String city;
    private long countryId;
    private double longitude;
    private double latitude;

    @Transient
    @Autowired private CountryRepository countryRepository;

    @Override
    public LocationDTO toDTO() {
        Country country = countryRepository.findById(countryId);
        return new LocationDTO(this.id, this.city, country.toDTO(), this.longitude, this.latitude);
    }

}
