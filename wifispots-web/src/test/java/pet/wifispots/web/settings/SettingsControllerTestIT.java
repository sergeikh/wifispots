package pet.wifispots.web.settings;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import pet.wifispots.WifispotsWebApplication;
import pet.wifispots.data.model.settings.Settings;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = WifispotsWebApplication.class)
@WebAppConfiguration
public class SettingsControllerTestIT {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testViewSettings() throws Exception {
        mockMvc.perform(get("/settings"))
                .andExpect(view().name("settings/settings"))
                .andExpect(model().attributeExists("settings", "spotsAdditionMode", "spotsViewMode", "usersRegistrationMode"));
    }

    @Test
    public void testUpdateSettings() throws Exception {
        mockMvc.perform(post("/settings").requestAttr("settings", new Settings()))
                .andExpect(redirectedUrl("/welcome"));
    }
}