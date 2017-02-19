package pet.wifispots.data.model.settings;

/**
 * Who can add new spots.
 */
public enum SpotsAdditionMode {
	EVERY_ONE("Every one"), ONLY_REGISTERED_USERS("Only registered users"), ONLY_ADMINISTRATORS("Only administrators");

    private String description;

    SpotsAdditionMode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
