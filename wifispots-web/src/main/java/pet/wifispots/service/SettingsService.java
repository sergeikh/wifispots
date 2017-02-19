package pet.wifispots.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import pet.wifispots.data.model.settings.Settings;
import pet.wifispots.data.repository.SettingsRepository;

import javax.annotation.PostConstruct;

@Service
@DependsOn("applicationInitializer")
public class SettingsService {
    @Autowired
    SettingsRepository settingsRepository;

    private Settings settings;

    @PostConstruct
    void onInit() { settings = settingsRepository.findOne(1l);
    }

    public Settings getSettings() {
        return settings;
    }
}
