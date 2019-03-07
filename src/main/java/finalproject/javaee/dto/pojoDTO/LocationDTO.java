package finalproject.javaee.dto.pojoDTO;

import finalproject.javaee.dto.pojoDTO.CountryDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LocationDTO {

    private long id;
    private String city;
    private CountryDTO country;
    private double longitude;
    private double latitude;

}
