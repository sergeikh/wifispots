package pet.wifispots.data.model.address;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Base class for all address components.
 */
@MappedSuperclass
public @Data class AddressComponent {

    @NotEmpty(message = "Address component place_id can't be empty.")
    @Column(unique = true)
    protected String placeId;
    @NotNull(message = "Address component name, cant' be null.")
    protected String name;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    protected Bounds bounds;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    protected Bounds viewport;
}
