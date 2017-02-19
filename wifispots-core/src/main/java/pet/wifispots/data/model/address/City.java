package pet.wifispots.data.model.address;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class City extends AddressComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    @JoinColumn
    private Country country;
    @OneToOne
    @JoinColumn
    private State state;
}
