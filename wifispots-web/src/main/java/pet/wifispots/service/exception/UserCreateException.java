package pet.wifispots.service.exception;

/**
 * New user creation exception: email is empty, email already registered.
 */
public class UserCreateException extends Exception {
    public UserCreateException(String msg) {
        super(msg);
    }
}
