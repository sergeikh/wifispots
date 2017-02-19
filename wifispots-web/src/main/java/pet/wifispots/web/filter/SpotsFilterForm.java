package pet.wifispots.web.filter;

import lombok.Data;
import pet.wifispots.data.model.Category;
import pet.wifispots.data.model.address.City;
import pet.wifispots.data.model.address.Country;
import pet.wifispots.data.model.address.State;

/**
 * Spots filter form.
 */
@Data
public class SpotsFilterForm {
    private boolean personal;
	private String name;
	private String userEmail;
	private String description;
	private String address;
	private Country country;
	private State state;
    private Category category;
	private City city;
	private String postalCode;
	private String url;
}