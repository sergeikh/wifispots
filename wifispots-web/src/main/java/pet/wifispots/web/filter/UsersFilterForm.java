package pet.wifispots.web.filter;

import lombok.Data;
import pet.wifispots.data.model.UserRole;

/**
 * User filter form.
 */
@Data
public class UsersFilterForm {
	private String email;
	private String status;
	private UserRole role;
}