package pet.wifispots.data.model.settings;

/**
 * Who can view not private spots.
 */
public enum SpotsViewMode {
	EVERY_ONE("Every one"), ONLY_REGISTERED("Only registered");

	private String description;

	SpotsViewMode(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
