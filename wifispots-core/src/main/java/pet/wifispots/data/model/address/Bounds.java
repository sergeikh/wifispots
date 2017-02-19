package pet.wifispots.data.model.address;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public @Data class Bounds implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="lat",column=@Column(name="NorthEastLat")),
            @AttributeOverride(name="lng",column=@Column(name="NorthEastLng"))
    })
    private Point northeast;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="lat",column=@Column(name="SouthWestLat")),
            @AttributeOverride(name="lng",column=@Column(name="SouthWestLng"))
    })
    private Point southwest;
}
