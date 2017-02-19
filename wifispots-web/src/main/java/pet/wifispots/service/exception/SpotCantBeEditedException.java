package pet.wifispots.service.exception;

/**
 * When spot is personal and edited by null user or user with role not admin
 * or user not equals to spot.user.
 */
public class SpotCantBeEditedException extends RuntimeException {
	public SpotCantBeEditedException(String msg) {
		super(msg);
	}
}
