package pet.wifispots;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pet.wifispots.data.model.Category;
import pet.wifispots.data.model.Spot;
import pet.wifispots.data.model.User;
import pet.wifispots.data.model.UserRole;
import pet.wifispots.data.model.address.Point;
import pet.wifispots.data.model.settings.Settings;
import pet.wifispots.data.repository.CategoryRepository;
import pet.wifispots.data.repository.SettingsRepository;
import pet.wifispots.data.repository.SpotRepository;
import pet.wifispots.data.repository.UserRepository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.IntStream;

/**
 * Loads default data for application.
 */
@Service
@Log
public class ApplicationInitializer {
	@Autowired
	UserRepository userRepository;
	@Autowired
	SpotRepository spotRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	SettingsRepository settingsRepository;
	@Autowired
	DataSource dataSource;
	@Autowired
	Environment env;
	
	private Settings settings;
	private User admin, user, blocked;
	private Spot userSpot, adminPersonalSpot, publicSpot;
    private Point point;
	
	@PostConstruct
	public void onInit() {
		if(userRepository.count() != 0) {
			log.info("Data is present. No data loading is performed.");
			
			return;
		}

        point = new Point();
        point.setLat(50.256796);
        point.setLng(127.521083);

		resetIdAutoincrement();
		initDefaultSettings();
		initUsers();
		initCategories();
		initSpots();

        init111User();
		init111Spots();
	}

    private void init111Spots() {
        Spot spot;

        for(int i = 0; i < 111; i++) {
            spot = new Spot();
            spot.setPersonal(false);
            spot.setName(String.format("Generated Spot #%d ", i));

            spotRepository.save(spot);
        }
    }

    private void init111User() {
		for (int i = 0; i < 121; i++) {
			User u = new User();
			u.setEmail(String.format("user%d@mail", i));
			u.setRole(UserRole.USER);

			userRepository.save(u);
		}
	}

	/**
	 * For integration test.
	 */
	private void resetIdAutoincrement() {
		// default profile only
		if(env.getActiveProfiles().length != 0)  {
			return;
		}

		try {
			dataSource.getConnection().createStatement().execute("ALTER TABLE USER ALTER COLUMN id RESTART WITH 1");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void initSpots() {
		userSpot = new Spot();
		userSpot.setDescription("user spot 1");
		userSpot.setName("user spot 1");
		userSpot.setPersonal(false);
        userSpot.setPoint(point);

        spotRepository.save(userSpot);

        user.addSpot(userSpot);
		userRepository.save(user);

		adminPersonalSpot = new Spot();
		adminPersonalSpot.setName("admin personal spot");
		adminPersonalSpot.setPersonal(true);
        adminPersonalSpot.setPoint(point);
		adminPersonalSpot.setUser(admin);

        spotRepository.save(adminPersonalSpot);

		admin.addSpot(adminPersonalSpot);
		userRepository.save(user);
		
		publicSpot = new Spot();
		publicSpot.setDescription("Public spot");
		publicSpot.setName("public spot");
		publicSpot.setUrl("http://test.url");
        publicSpot.setPoint(point);
		
		spotRepository.save(publicSpot);
		
		log.info("Spots are loaded");
	}

	private void initCategories() {
		String[] categories = {"Airport", "Arts and Entertainment", "Automotive", "Baby / Kid Boutique",
			"Bagel Store", "Bakery", "Bar and Grill", "Beach", "Billiard Hall", "Boat or Ferry", "Bookstore",
			"Brewery", "Brewpub", "Bus Station", "Business Center", "Campground", "Charity", "Citywide Network",
			"Clothing / Fashion Store", "Coffeeshop / Cafe", "Comedy Club", "Convention Center",
			"Cosmetic / Beauty Store", "Downtown Area", "Farmers Market", "Fashion Accessories", "Fast Food",
			"Florist", "Food / Grocery Store", "Garden / Nursery Store", "Gas Station", "General Interest Schools",
			"Golf Course", "Government Building", "Health / Fitness Club", "Home / Furniture Store", "Hospital / Doctor Office",
			"Hotel / Motel / Resort", "Ice Cream / Dessert", "Internet Cafe", "Juice / Smoothy Store", "Kiosk", "Laundromat / Dry Cleaner",
			"Library", "Marina", "Movie Theater", "Museum", "Nightlife", "Office Building", "Office Park", "Other", "Park",
			"Bakery", "Pet Daycare", "Pet Groomer / Spa", "Pet Store", "Pharmacy", "Private Club", "Public Space", "Recreation",
			"Residential Area", "Rest Area", "Restaurant", "RV Park", "Salon / Spa", "Service", "Services", "Shopping Mall",
			"Sports Venue", "Store", "Tourist Attraction", "Toy Store", "Train Station", "Travel Center", "University", "School",
			"Wholesaler", "Wine Bar", "Wine Shop", "Winery", "Zoo", "Aquarium"};
		
		Category[] cat = new Category[categories.length];
		
		IntStream
			.range(0, categories.length)
			.forEach(i -> cat[i] = new Category(categories[i]));
		
		categoryRepository.save(Arrays.asList(cat));
		
		log.info("Categories are loaded.");
	}

	private void initUsers() {
		admin = new User();
		admin.setBlocked(false);
		admin.setSince(new Date());
		admin.setEmail("demo@admin");
		admin.setPassword("pwd");
		admin.setRole(UserRole.ADMINISTRATOR);
		
		user = new User();
		user.setBlocked(false);
		user.setSince(new Date());
		user.setEmail("demo@user");
		user.setPassword("pwd");
		user.setRole(UserRole.USER);

		blocked = new User();
		blocked.setEmail("Blocked@mail");
		blocked.setBlocked(true);

		userRepository.save(Arrays.asList(admin, user, blocked));
		
		log.info("Users are loaded.");
	}

	private void initDefaultSettings() {
		settings = new Settings();
		settingsRepository.save(settings);
		
		log.info("Settings are loaded.");
	}
}
