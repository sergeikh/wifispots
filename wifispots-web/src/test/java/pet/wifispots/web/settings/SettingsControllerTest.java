package pet.wifispots.web.settings;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pet.wifispots.data.model.settings.Settings;
import pet.wifispots.data.model.settings.SpotsAdditionMode;
import pet.wifispots.data.repository.SettingsRepository;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class SettingsControllerTest {
    private SettingsController cut;
    private Settings settings;
    private MockMvc mockMvc;

    @Mock
    SettingsRepository settingsRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        settings = new Settings();
        settings.setAdditionMode(SpotsAdditionMode.ONLY_REGISTERED_USERS);

        cut = new SettingsController();
        cut.settingsRepository = settingsRepository;

        mockMvc =  MockMvcBuilders.standaloneSetup(cut).build();
    }

    @Test
    public void testViewSettings() throws Exception {
        when(settingsRepository.findOne(1l)).thenReturn(settings);

        mockMvc.perform(get("/settings"))
            .andExpect(status().isOk()).andDo(print())
            .andExpect(view().name("settings/settings"))
            .andExpect(model().attribute("settings", is(settings)))
            .andExpect(model().attribute("settings", hasProperty("additionMode", is(SpotsAdditionMode.ONLY_REGISTERED_USERS))));

        verify(settingsRepository, times(1)).findOne(1L);
    }

    @Test
    public void testUpdateSettings() throws  Exception {
        settings.setId(1l);
        settings.setReverseGeocoderURL("test");

        mockMvc.perform(post("/settings")
                .param("reverseGeocoderURL", "test")
                .param("additionMode", "ONLY_REGISTERED")
                .param("viewMode", "EVERY_ONE")
                .param("registrationMode", "EVERY_ONE"))
            .andExpect(status().is3xxRedirection());

        verify(settingsRepository, times(1)).save(settings);
    }

}