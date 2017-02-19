package pet.wifispots.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController {
	@RequestMapping(value = { "/", "/home", "/welcome" }, method = RequestMethod.GET)
	public String getIndexPage() {

		return "index";
	}

	@RequestMapping(value = { "/contact" }, method = RequestMethod.GET)
	public String getContactsPage(Model model) {

		return "contact";
	}

	@RequestMapping(value = { "/login" }, method = RequestMethod.GET)
	public String getLoginPage() {

		return "login";
	}

}
