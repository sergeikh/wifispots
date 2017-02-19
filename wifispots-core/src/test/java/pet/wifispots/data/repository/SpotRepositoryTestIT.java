package pet.wifispots.data.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import pet.wifispots.data.model.Spot;
import pet.wifispots.data.model.User;
import wifispots.data.configuration.JpaConfiguration;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JpaConfiguration.class)
public class SpotRepositoryTestIT {
    private static final String PUBLIC_SPOT_NAME = "Public spot";
    private static final String PERSONAL_USER1_SPOT_NAME = "Personal spot user1";
    private static final String PERSONAL_USER2_SPOT_NAME = "Personal spot user2";
    @Autowired
    SpotRepository spotRepository;

    @Autowired
    UserRepository userRepository;

    Spot publicSpot, personalSpotUser1, personalSpotUser2;
    User user1, user2;

    @Before
    public void init() {
        user1 = new User();
        user1.setEmail("user12@email.com");

        user2 = new User();
        user2.setEmail("user22@email.com");

        userRepository.save(Arrays.asList(user1, user2));

        publicSpot = new Spot();
        publicSpot.setName(PUBLIC_SPOT_NAME);
        publicSpot.setPersonal(false);

        personalSpotUser1 = new Spot();
        personalSpotUser1.setName(PERSONAL_USER1_SPOT_NAME);
        personalSpotUser1.setPersonal(true);
        personalSpotUser1.setUser(user1);

        personalSpotUser2 = new Spot();
        personalSpotUser2.setName(PERSONAL_USER2_SPOT_NAME);
        personalSpotUser2.setPersonal(true);
        personalSpotUser2.setUser(user2);

        spotRepository.save(Arrays.asList(publicSpot, personalSpotUser1, personalSpotUser2));
    }

    @Test
    @Transactional
    public void testShouldFindAllNotPersonalSpotsPlusPersonalUserSpots() {
        List<Spot> user1Spots = spotRepository.findByPersonalIsFalseOrPersonalIsTrueAndUser(user1);
        assertEquals(user1Spots.size(), 2);

        assertEquals(user1Spots.get(0).getName(), PUBLIC_SPOT_NAME);
        assertEquals(user1Spots.get(1).getName(), PERSONAL_USER1_SPOT_NAME);
        assertThat(user1Spots, hasItems(publicSpot, personalSpotUser1));

        List<Spot> user2Spots = spotRepository.findByPersonalIsFalseOrPersonalIsTrueAndUser(user2);
        assertEquals(user2Spots.size(), 2);

        assertEquals(user2Spots.get(0).getName(), PUBLIC_SPOT_NAME);
        assertEquals(user2Spots.get(1).getName(), PERSONAL_USER2_SPOT_NAME);
    }

    @Test
    @Transactional
    public void testShouldFindAllPublicNotPersonalSpots() {
        List<Spot> publicSpots = spotRepository.findByPersonalIsFalse();
        assertEquals(publicSpots.size(), 1);
        assertEquals(publicSpots.get(0).getName(), PUBLIC_SPOT_NAME);
        assertThat(publicSpots, hasItems(publicSpot));
    }

    @Test
    @Transactional
    public void testShouldFindOnlyUserPersonalSpots() {
        List<Spot> user1Spots = spotRepository.findByPersonalIsTrueAndUser(user1);
        assertEquals(user1Spots.size(), 1);
        assertEquals(user1Spots.get(0).getName(), PERSONAL_USER1_SPOT_NAME);

        List<Spot> user2Spots = spotRepository.findByPersonalIsTrueAndUser(user2);
        assertEquals(user2Spots.size(), 1);
        assertEquals(user2Spots.get(0).getName(), PERSONAL_USER2_SPOT_NAME);

        assertThat(user2Spots, hasItems(personalSpotUser2));
    }

}