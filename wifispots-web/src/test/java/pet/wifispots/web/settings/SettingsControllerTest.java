package pet.wifispots.web.settings;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import pet.wifispots.data.model.settings.Settings;
import pet.wifispots.data.model.settings.SpotsAdditionMode;
import pet.wifispots.data.repository.SettingsRepository;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class SettingsControllerTest {
    private SettingsController cut;
    private Settings settings;
    private Model model;

    @Mock
    SettingsRepository settingsRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        settings = new Settings();
        settings.setAdditionMode(SpotsAdditionMode.ONLY_REGISTERED_USERS);

        cut = new SettingsController();
        cut.settingsRepository = settingsRepository;

        model = new ExtendedModelMap();
    }

    @Test
    public void testViewSettings() throws Exception {
        when(settingsRepository.findOne(1l)).thenReturn(settings);

        assertEquals(cut.editSettings(model), "settings/settings");
        assertThat(model.asMap().keySet(), CoreMatchers.hasItems("settings", "usersRegistrationMode") );

        verify(settingsRepository, times(1)).findOne(1L);
    }

    @Test
    public void testUpdateSettings() throws  Exception {
        settings.setReverseGeocoderURL("test");

        assertEquals(cut.updateSettings(settings), "redirect:/welcome");

        assertEquals((long) settings.getId(), 1);

        verify(settingsRepository, times(1)).save(settings);
    }

}