package pet.wifispots.data.repository.address;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import pet.wifispots.data.model.address.State;

import java.util.Optional;

public interface StateRepository extends JpaRepository<State, Long>, QueryDslPredicateExecutor<State> {
    Optional<State> findByPlaceId(String placeId);
}
