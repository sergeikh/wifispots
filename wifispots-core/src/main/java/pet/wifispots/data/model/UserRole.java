package pet.wifispots.data.model;

public enum UserRole {
	ADMINISTRATOR("Administrator"), USER("User");
	
	private String name;
	private UserRole(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
