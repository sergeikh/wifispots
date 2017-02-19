package pet.wifispots.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pet.wifispots.data.model.User;
import pet.wifispots.data.repository.UserRepository;
import pet.wifispots.data.specification.UserSpecification;
import pet.wifispots.service.exception.UserCreateException;

import java.util.Objects;

@Service
@Log
public class UserService {
	@Autowired
	UserRepository userRepository;
	
	/**
	 * Create new user; reset: id, since.
	 */
	public User create(User user) throws UserCreateException {
		user.setId(null);
		user.setSince(null);
		user.setLastLogin(null);
		user.setBlocked(false);
		user.setSpots(null);

		validateUser(user);

		User saved = userRepository.save(user);
		sendNewPassword(saved);
		
		return saved;		
	}

	private void validateUser(User user) throws UserCreateException {
		if(Objects.isNull(user.getEmail()) || user.getEmail().trim().isEmpty()) {
			throw new UserCreateException("User email can't be empty.");
		}

		user.setEmail(user.getEmail().trim());

		User fromDB = userRepository.findOne(UserSpecification.isEmailEqualsIgnoreCase(user.getEmail()));
		if(!Objects.isNull(fromDB))
			throw new UserCreateException(String.format("User with email %s, already registered.", fromDB.getEmail()));
	}

	/**
	 * Delete user.
	 */
	public void delete(User user) {
		userRepository.delete(user);
	}
	
	/**
	 * Update status, role.
	 */
	public User update(User user) {
		User fromDB = userRepository.findOne(user.getId());
		fromDB.setBlocked(user.isBlocked());
		fromDB.setRole(user.getRole());
		
		return userRepository.save(fromDB);
	}
	
	/**
	 * Send new password for user.
	 */
	public void sendNewPassword(User user) {

	}
}
