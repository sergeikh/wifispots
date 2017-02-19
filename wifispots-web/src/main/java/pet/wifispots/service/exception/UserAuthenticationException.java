package pet.wifispots.service.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * User details is not correct for authentication. 
 */
public class UserAuthenticationException extends AuthenticationException {
	public UserAuthenticationException(String msg) {
        super(msg);
    }
}
