package pet.wifispots.data.repository.address;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import pet.wifispots.data.model.address.City;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long>, QueryDslPredicateExecutor<City> {
    Optional<City> findByPlaceId(String placeId);
}
