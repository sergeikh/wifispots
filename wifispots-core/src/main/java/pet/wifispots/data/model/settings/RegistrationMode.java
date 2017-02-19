package pet.wifispots.data.model.settings;

/**
 * User registration mode.
 */
public enum RegistrationMode {
	EVERY_ONE("Every one"), ADMINISTRATOR_ONLY("Administrator only");

	private String description;

	RegistrationMode(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
