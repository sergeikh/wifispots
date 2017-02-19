package pet.wifispots.service.exception;

/**
 * When user is not found by id.
 */
public class UserNotFoundException extends RuntimeException {	
	public UserNotFoundException (String msg) {
		super(msg);
	}
}
