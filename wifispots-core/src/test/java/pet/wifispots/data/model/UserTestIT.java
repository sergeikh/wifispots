package pet.wifispots.data.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import pet.wifispots.data.repository.SpotRepository;
import pet.wifispots.data.repository.UserRepository;
import wifispots.data.configuration.JpaConfiguration;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JpaConfiguration.class)
public class UserTestIT {
    @Autowired
    UserRepository userRepository;

    @Autowired
    SpotRepository spotRepository;

    @Autowired
    PlatformTransactionManager transactionManager;

    User user;
    Spot spot;

    @Test
    public void shouldCorrectlyPersistUsersSpot() {
        TransactionStatus transactionStatus = transactionManager.getTransaction(null);

        // add new users spot
        user = new User();
        user.setEmail("demo@admin");

        spot = new Spot();
        spot.setName("Demo spot");
        spot.setUser(user);

        userRepository.save(user);
        spotRepository.save(spot);

        transactionManager.commit(transactionStatus);

        // validate user.spots should not be empty
        transactionStatus = transactionManager.getTransaction(null);

        Spot spotDB = spotRepository.findOne(spot.getId());
        User userDB = userRepository.findOne(user.getId());

        assertEquals(spotDB.getUser().getEmail(), user.getEmail());
        assertEquals(userDB.getSpots().size(), 1);

        // cleanup
        userRepository.delete(user.getId());
        spotRepository.delete(spot.getId());
        transactionManager.commit(transactionStatus);
    }
}