package pet.wifispots.data.model.address;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public @Data class Point implements Serializable {
    @Column(nullable = true)
    private double lat;
    @Column(nullable = true)
    private double lng;
}
