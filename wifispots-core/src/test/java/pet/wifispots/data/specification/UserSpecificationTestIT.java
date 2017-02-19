package pet.wifispots.data.specification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import pet.wifispots.data.model.User;
import pet.wifispots.data.model.UserRole;
import pet.wifispots.data.repository.UserRepository;
import wifispots.data.configuration.JpaConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.isNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JpaConfiguration.class)
public class UserSpecificationTestIT {
    @Autowired
    UserRepository userRepository;

    @Before
    public void init() {
        User user = new User();
        user.setEmail("demo@admin");
        user.setBlocked(false);
        user.setRole(UserRole.ADMINISTRATOR);

        userRepository.save(user);
    }

    @Test
    @Transactional
    public void shouldFindUserByEmailAndNotBlocked () {
        User fromDB = userRepository.findOne(UserSpecification.isEmailEqualsIgnoreCase("demo@admin").and(UserSpecification.isBlocked(false)));

        assertThat(fromDB, not(isNull()));
    }

    @Test
    @Transactional
    public void shouldFindEmailByPart() {
        User fromDB = userRepository.findOne(UserSpecification.isEmailContains("demo"));
        assertThat(fromDB, not(isNull()));
    }

    @Test
    @Transactional
    public void shouldFindUserByRole() {
        User adminFromDB = userRepository.findOne(UserSpecification.isRole(UserRole.ADMINISTRATOR));
        User userFromDB = userRepository.findOne(UserSpecification.isRole(UserRole.USER));

        assertThat(adminFromDB, not(isNull()));
        assertThat(userFromDB, is(nullValue()));
    }


}