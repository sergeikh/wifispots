package pet.wifispots.web.users;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pet.wifispots.data.model.User;
import pet.wifispots.data.model.UserRole;
import pet.wifispots.data.repository.UserRepository;
import pet.wifispots.data.specification.UserSpecification;
import pet.wifispots.service.ActiveUser;
import pet.wifispots.service.UserService;
import pet.wifispots.service.exception.UserCreateException;
import pet.wifispots.service.exception.UserNotFoundException;
import pet.wifispots.web.filter.UsersFilterForm;
import pet.wifispots.web.utils.PageWrapper;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Objects;

@Controller
@Log
public class UsersController {
	@Autowired
	UserRepository userRepository;

	@Autowired
	UserService userService;

	@Autowired
	ActiveUser activeUser;
	
	@RequestMapping(value="/users", method=RequestMethod.GET)
	public String getUsers(Model model, Pageable p) {

		// filter users
        if (model.containsAttribute("usersFilterForm")) {
            UsersFilterForm uff = (UsersFilterForm) model.asMap().get("usersFilterForm");
            BooleanExpression filterExpr = null;
            
            if(Objects.nonNull(uff.getEmail()) && !uff.getEmail().trim().isEmpty())
            	filterExpr = UserSpecification.isEmailContains(uff.getEmail().trim());
            
            if(Objects.nonNull(uff.getRole()))
            	if(Objects.nonNull(filterExpr)) {
            		filterExpr = filterExpr.and(UserSpecification.isRole(uff.getRole()));
            	} else {
            		filterExpr = UserSpecification.isRole(uff.getRole());
            	}
            
            if(Objects.nonNull(uff.getStatus()) && !uff.getStatus().trim().isEmpty()) {
            	if(Objects.nonNull(filterExpr)) {
            		filterExpr = filterExpr.and(UserSpecification.isBlocked(uff.getStatus().equals("BLOCKED")));
            	} else {
            		filterExpr = UserSpecification.isBlocked(uff.getStatus().equals("BLOCKED"));
            	}
            }            

            model.addAttribute("usersFilterForm", uff);
            model.addAttribute("page", new PageWrapper<User>(userRepository.findAll(filterExpr, p), "/users"));
        } else {
            model.addAttribute("usersFilterForm", new UsersFilterForm());
            model.addAttribute("page", new PageWrapper<User>(userRepository.findAll(p), "/users"));
        }

        model.addAttribute("statuses", Arrays.asList("ACTIVE", "BLOCKED"));
		addRolesModelAttribute(model);

		return "users/list";
	}
	
	@RequestMapping(value="/users/{id}", method=RequestMethod.GET)
	public String editUser(Model model, @PathVariable("id") Long id) {
		User user = userRepository.findOne(id);
		
		if(Objects.isNull(user))
			throw new UserNotFoundException(String.format("User by id=%d is not found.", id));
		
		model.addAttribute("user", user);
		addRolesModelAttribute(model);

		return "users/user";	
	}
	
	@RequestMapping(value="/register", method=RequestMethod.GET)
	public String getNewUser(Model model) {
		model.addAttribute("user", new User());
		addRolesModelAttribute(model);

		return "users/register";
	}

	@RequestMapping(value="/register", method=RequestMethod.POST)
	public String createUser(RedirectAttributes model, @Valid User user, BindingResult bindingResults) {
		if(bindingResults.hasErrors()) {
			model.addFlashAttribute("user", new User());
			model.addFlashAttribute("error", bindingResults);
			return "redirect:/register";
		}

		// reset role is not Administrator
		if(Objects.isNull(activeUser.getUser()) || activeUser.getUser().getRole() != UserRole.ADMINISTRATOR) {
			user.setRole(UserRole.USER);
		}

		try {
			userService.create(user);

			return "redirect:/welcome";
		} catch (UserCreateException ex) {
			bindingResults.addError(new FieldError("user", "email", ex.getMessage()));
			model.addFlashAttribute("error", bindingResults);
			model.addFlashAttribute("user", new User());
		}

		return "redirect:/register";
	}
	
	@RequestMapping(value="/users/{id}", method=RequestMethod.POST)
	public String updateUser(@PathVariable("id") Long id, User user) {
		if(id != user.getId())
			throw new UserNotFoundException(String.format("User.id=%d is not same as post parameter id=%d.", user.getId(), id));

		userService.update(user);

		return "redirect:/users";
	}

    /**
     * Send new password for registered user.
     */
    @RequestMapping(value = {"/users/{id}"}, method = RequestMethod.POST, params = {"sendNewPassword"})
    public String sendNewPassword(@PathVariable("id") Long id, User user) {
        if(id != user.getId())
            throw new UserNotFoundException(String.format("User.id=%d is not same as post parameter id=%d.", user.getId(), id));

        userService.sendNewPassword(user);

        return "redirect:/users";
    }

    /**
     * Filter user list by attributes. 
     */
    @RequestMapping(value = {"/users/filter"}, method = RequestMethod.POST)
    public String filterUsers(UsersFilterForm usersFilterForm, RedirectAttributes model) {
        model.addFlashAttribute("usersFilterForm", usersFilterForm);

        return "redirect:/users";
    }

	private void addRolesModelAttribute(Model model) {
		model.addAttribute("roles", UserRole.values());
	}
}
