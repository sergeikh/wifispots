package pet.wifispots.data.model.settings;

import lombok.Data;

import javax.persistence.*;

/**
 * Application settings.
 */
@Entity
public @Data class Settings {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Enumerated(EnumType.STRING)
	private SpotsAdditionMode additionMode = SpotsAdditionMode.EVERY_ONE;
	@Enumerated(EnumType.STRING)
	private SpotsViewMode viewMode = SpotsViewMode.EVERY_ONE;
	@Enumerated(EnumType.STRING)
	private RegistrationMode registrationMode = RegistrationMode.EVERY_ONE;
	private String reverseGeocoderURL = "http://localhost:8082/v1/rgeocode/{spotId}";
}
