package pet.wifispots.data.model.address;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public @Data class Country extends AddressComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}
