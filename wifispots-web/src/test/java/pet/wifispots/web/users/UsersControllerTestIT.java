package pet.wifispots.web.users;

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
import pet.wifispots.data.model.User;
import pet.wifispots.data.model.UserRole;
import pet.wifispots.data.repository.UserRepository;
import pet.wifispots.web.filter.UsersFilterForm;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = WifispotsWebApplication.class)
@WebAppConfiguration
public class UsersControllerTestIT {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    UserRepository userRepository;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGetUsersList() throws Exception {
        mockMvc.perform(get("/users")).andDo(print())
            .andExpect(view().name("users/list"))
            .andExpect(model().attributeExists("usersFilterForm", "statuses", "roles", "page"))
            .andExpect(model().attribute("statuses", hasItems("ACTIVE", "BLOCKED")))
            .andExpect(model().attribute("page", hasProperty("totalPages", equalTo(6))));
    }

    @Test
    public void testFilterUsersList() throws Exception {
        UsersFilterForm usersFilterForm = new UsersFilterForm();
        usersFilterForm.setEmail("demo@admin");

        mockMvc.perform(get("/users").flashAttr("usersFilterForm", usersFilterForm))
                .andExpect(view().name("users/list"))
                .andExpect(model().attribute("page", hasProperty("content", hasSize(1))));
    }

    @Test
    public void testEditUser() throws Exception {
        mockMvc.perform(get("/users/1"))
            .andExpect(view().name("users/user"))
            .andExpect(model().attribute("user", hasProperty("email", equalTo("demo@admin"))));
    }

    @Test
    public void testUpdateUser() throws Exception {
        User user2 = new User();
        user2.setRole(UserRole.ADMINISTRATOR);
        user2.setBlocked(true);

        mockMvc.perform(post("/users/2").requestAttr("user", user2))
            .andExpect(redirectedUrl("/users"));
    }

    @Test
    public void testGetNewUser() throws Exception {
        mockMvc.perform(get("/register"))
            .andExpect(status().isOk())
            .andExpect(view().name("users/register"))
            .andExpect(model().attributeExists("user"))
            .andExpect(model().attribute("user", hasProperty("id", equalTo(null))))
            .andExpect(model().attribute("error", isEmptyOrNullString()));
    }

    @Test
    public void testCreateNewGoodUser() throws Exception {
        mockMvc.perform(post("/register").param("email", "ddd@mail"))
            .andExpect(redirectedUrl("/welcome"));
    }

    @Test
    public void testCreateBadEmail() throws Exception {
        mockMvc.perform(post("/register").param("email", "ddd"))
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attribute("user", hasProperty("id", isEmptyOrNullString())))
                .andExpect(flash().attribute("error", not(isEmptyOrNullString())));
    }

    @Test
    public void testCreateSameEmail() throws Exception {
        mockMvc.perform(post("/register").param("email", "demo@admin"))
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attribute("user", hasProperty("id", isEmptyOrNullString())))
                .andExpect(flash().attribute("error", not(isEmptyOrNullString())));
    }

}