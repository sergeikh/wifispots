package pet.wifispots.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pet.wifispots.data.model.User;
import pet.wifispots.data.model.UserRole;
import pet.wifispots.data.repository.UserRepository;
import pet.wifispots.data.specification.UserSpecification;
import pet.wifispots.service.exception.UserCreateException;

import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

public class UserServiceTest {
	UserService cut;
    User beforeSave, afterSave;

    @Mock
	UserRepository userRepository;
	
	@Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        cut = new UserService();
        cut.userRepository = userRepository;

        beforeSave = new User();
        beforeSave.setEmail("test@email");
        beforeSave.setBlocked(false);
        beforeSave.setRole(UserRole.ADMINISTRATOR);
        beforeSave.setId(1l);
        beforeSave.setSince(new Date());

        afterSave = new User();
        afterSave.setEmail("test@email");
        afterSave.setBlocked(false);
        afterSave.setRole(UserRole.ADMINISTRATOR);
        afterSave.setId(null);
        afterSave.setSince(null);
    }
	
	@Test
    public void testDeleteUser() {
        doNothing().when(userRepository).delete(beforeSave);

        cut.delete(beforeSave);

        verify(userRepository, only()).delete(beforeSave);
    }

    @Test
    public void testUpdateUser() {
        beforeSave.setId(1l);
        beforeSave.setBlocked(true);
        beforeSave.setRole(UserRole.USER);

        when(userRepository.findOne(1l)).thenReturn(afterSave);
        when(userRepository.save(afterSave)).thenReturn(afterSave);

        User fromDB = cut.update(beforeSave);

        verify(userRepository).findOne(1l);
        verify(userRepository).save(afterSave);

        assertEquals(fromDB.isBlocked(), true);
        assertEquals(fromDB.getRole(), UserRole.USER);
    }

    @Test
    public void testCreateUser() throws Exception {
        when(userRepository.save(beforeSave)).thenReturn(afterSave);
        when(userRepository.findOne(UserSpecification.isEmailEqualsIgnoreCase(beforeSave.getEmail()))).thenReturn(null);

        cut.create(beforeSave);

        InOrder inOrder = inOrder(userRepository);

        inOrder.verify(userRepository).findOne(UserSpecification.isEmailEqualsIgnoreCase(beforeSave.getEmail()));
        inOrder.verify(userRepository).save(beforeSave);

        inOrder.verifyNoMoreInteractions();
    }

    @Test(expected = UserCreateException.class)
    public void testCreateUserEmptyEmail() throws Exception {
        beforeSave.setEmail("");
        cut.create(beforeSave);
    }

    @Test(expected = UserCreateException.class)
    public void testCreateUserNullEmail() throws Exception {
        beforeSave.setEmail(null);
        cut.create(beforeSave);
    }

    @Test(expected = UserCreateException.class)
    public void testCreateUserExistingEmail() throws Exception {
        beforeSave.setEmail("demo@admin");
        when(userRepository.findOne(UserSpecification.isEmailEqualsIgnoreCase(beforeSave.getEmail()))).thenReturn(afterSave);

        cut.create(beforeSave);
    }
}
