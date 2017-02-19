package pet.wifispots.data.model.address;

import lombok.Data;

import javax.persistence.*;

@Entity
public @Data class State extends AddressComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    @JoinColumn
    private Country country;
}
