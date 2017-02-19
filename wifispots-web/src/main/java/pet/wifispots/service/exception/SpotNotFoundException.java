package pet.wifispots.service.exception;

/**
 * When spot is not found by id.
 */
public class SpotNotFoundException extends RuntimeException {
	public SpotNotFoundException(String msg) {
		super(msg);
	}
}
