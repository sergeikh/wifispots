package pet.wifispots.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pet.wifispots.data.model.Category;
import pet.wifispots.data.model.Spot;
import pet.wifispots.data.model.User;
import pet.wifispots.data.model.UserRole;
import pet.wifispots.data.model.address.Point;
import pet.wifispots.data.repository.SpotRepository;
import pet.wifispots.service.exception.SpotCantBeEditedException;
import pet.wifispots.service.exception.SpotNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class SpotServiceTest {
    SpotService cut;
    Spot before, after;
    User aUser, otherUser;
    Point point;

    @Mock
    SpotRepository spotRepository;
    @Mock
    ActiveUser activeUser;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        cut = new SpotService();
        cut.spotRepository = spotRepository;
        cut.activeUser = activeUser;

        Category category = new Category();
        category.setId(1l);
        category.setName("test cat");

        before = new Spot();
        before.setCategory(category);
        before.setId(1L);
        before.setName("test");
        before.setDescription("test");
        before.setPassword("123");
        before.setUrl("url");
        before.setPoint(point);

        after = new Spot();
        after.setId(1l);

        point = new Point();
        point.setLat(1L);
        point.setLat(2L);
    }

    @Test
    public void shouldSuccessfullyUpdateSpotWOPointForUser() {
        aUser = new User();
        aUser.setRole(UserRole.USER);

        when(spotRepository.findOne(1l)).thenReturn(after);
        when(spotRepository.save(after)).thenReturn(after);
        when(activeUser.getUser()).thenReturn(aUser);

        Spot afterUpdate = cut.updateSpot(before);

        assertEquals(afterUpdate, after);
        assertNull(after.getPoint());
        assertEquals(after.getName(), before.getName());
        assertEquals(after.getCategory(), before.getCategory());

        verify(spotRepository, times(1)).findOne(1L);
        verify(spotRepository, times(1)).save(after);
        verify(activeUser, times(1)).getUser();
    }

    @Test
    public void shouldSuccessfullyUpdateSpotWithPointForUserAdministrator() {
        aUser = new User();
        aUser.setRole(UserRole.ADMINISTRATOR);

        when(spotRepository.findOne(1l)).thenReturn(after);
        when(spotRepository.save(after)).thenReturn(after);
        when(activeUser.getUser()).thenReturn(aUser);

        Spot afterUpdate = cut.updateSpot(before);

        assertEquals(afterUpdate, after);
        assertEquals(before, after);
        assertEquals(before.getPoint(), after.getPoint());

        verify(spotRepository, times(1)).findOne(1L);
        verify(spotRepository, times(1)).save(after);
        verify(activeUser, times(1)).getUser();
    }

    @Test(expected = SpotNotFoundException.class)
    public void shouldThrowExceptionOnNotFoundSpot() {
        when(spotRepository.findOne(1l)).thenReturn(null);
        when(activeUser.getUser()).thenReturn(aUser);

        cut.updateSpot(before);

        verify(spotRepository, times(1)).findOne(1L);
        verifyNoMoreInteractions(spotRepository);
    }

    @Test(expected = SpotCantBeEditedException.class)
    public void cantUpdatePersonalSpotWithEmptyUser() {
        after.setPersonal(true);
        when(spotRepository.findOne(1l)).thenReturn(after);
        when(activeUser.getUser()).thenReturn(null);

        cut.updateSpot(before);

        verify(spotRepository, times(1)).findOne(1L);
        verify(activeUser, times(1)).getUser();
        verifyNoMoreInteractions(spotRepository);
    }

    @Test(expected = SpotCantBeEditedException.class)
    public void cantUpdatePersonalSpotWithOtherUserAndRoleUser() {
        aUser = new User();
        aUser.setRole(UserRole.USER);
        aUser.setId(1l);

        otherUser = new User();
        otherUser.setRole(UserRole.USER);
        otherUser.setId(2l);

        after.setPersonal(true);
        after.setUser(aUser);

        when(spotRepository.findOne(1l)).thenReturn(after);
        when(activeUser.getUser()).thenReturn(otherUser);

        cut.updateSpot(before);

        verify(spotRepository, times(1)).findOne(1L);
        verify(activeUser, times(1)).getUser();
        verifyNoMoreInteractions(spotRepository);
    }

    @Test
    public void updatePersonalSpotWithOtherUserAndRoleAdministrator() {
        aUser = new User();
        aUser.setRole(UserRole.USER);
        aUser.setId(1l);

        otherUser = new User();
        otherUser.setRole(UserRole.ADMINISTRATOR);
        otherUser.setId(2l);

        after.setPersonal(true);
        after.setUser(aUser);

        when(spotRepository.findOne(1l)).thenReturn(after);
        when(activeUser.getUser()).thenReturn(otherUser);
        when(spotRepository.save(after)).thenReturn(after);

        cut.updateSpot(before);

        verify(spotRepository, times(1)).findOne(1L);
        verify(activeUser, times(3)).getUser();
        verify(spotRepository, times(1)).save(after);

        assertEquals(before.getName(), after.getName());
    }
}