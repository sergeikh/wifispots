package pet.wifispots.data.repository.address;

import org.springframework.data.jpa.repository.JpaRepository;
import pet.wifispots.data.model.address.Country;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByPlaceId(String placeId);
}
