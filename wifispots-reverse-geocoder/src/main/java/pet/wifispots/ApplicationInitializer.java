package pet.wifispots;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pet.wifispots.data.model.Spot;
import pet.wifispots.data.model.address.Point;
import pet.wifispots.data.repository.SpotRepository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Loads default data for application.
 */
@Service
@Log
public class ApplicationInitializer {
	@Autowired
	SpotRepository spotRepository;
	@Autowired
	DataSource dataSource;
	@Autowired
	Environment env;

	private Spot userSpot;
	
	@PostConstruct
	public void onInit() {
        log.info("Application init.");
        if(spotRepository.count() != 0) {
			log.info("Data is present. No data loading is performed.");
			
			return;
		}

		resetIdAutoincrement();
		initSpots();		
	}

	/**
	 * For integration test.
	 */
	public void resetIdAutoincrement() {
		// default profile only
		if(env.getActiveProfiles().length != 0)  {
			return;
		}
		try {
			dataSource.getConnection().createStatement().execute("ALTER TABLE SPOT ALTER COLUMN id RESTART WITH 1");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void initSpots() {
		userSpot = new Spot();
		userSpot.setDescription("user spot 1");
		userSpot.setName("Reverse geocode 1");
		userSpot.setPersonal(false);
		Point point = new Point();
		point.setLat(50.256796);
		point.setLng(127.521083);
		userSpot.setPoint(point);

		spotRepository.save(userSpot);
		
		log.info("Spots are loaded");
	}
}
