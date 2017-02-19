package pet.wifispots.web.settings;

import groovy.util.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pet.wifispots.data.model.settings.RegistrationMode;
import pet.wifispots.data.model.settings.Settings;
import pet.wifispots.data.model.settings.SpotsAdditionMode;
import pet.wifispots.data.model.settings.SpotsViewMode;
import pet.wifispots.data.repository.SettingsRepository;

@Controller
@Log
public class SettingsController {
    @Autowired
    SettingsRepository settingsRepository;

    @RequestMapping(value="/settings", method= RequestMethod.GET)
    public String editSettings(Model model) {
        Settings settings = settingsRepository.findOne(1l);

        model.addAttribute("settings", settings);
        model.addAttribute("spotsAdditionMode", SpotsAdditionMode.values());
        model.addAttribute("spotsViewMode", SpotsViewMode.values());
        model.addAttribute("usersRegistrationMode", RegistrationMode.values());

        return "settings/settings";
    }

    @RequestMapping(value="/settings", method= RequestMethod.POST)
    public String updateSettings(Settings settings) {
        settings.setId(1l);
        settingsRepository.save(settings);

        return "redirect:/welcome";
    }
}
