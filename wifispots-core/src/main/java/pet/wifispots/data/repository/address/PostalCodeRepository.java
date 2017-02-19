package pet.wifispots.data.repository.address;

import org.springframework.data.jpa.repository.JpaRepository;
import pet.wifispots.data.model.address.PostalCode;

import java.util.Optional;

public interface PostalCodeRepository extends JpaRepository<PostalCode, Long> {
    Optional<PostalCode> findByPlaceId(String placeId);
}
