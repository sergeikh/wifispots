package pet.wifispots.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import pet.wifispots.data.model.Spot;
import pet.wifispots.data.model.User;

import java.util.List;

public interface SpotRepository extends JpaRepository<Spot, Long>, QueryDslPredicateExecutor<Spot> {
    /**
     * Find all public spots and user personal.
     * @param user user for personal spots
     * @return public and user personal spots list
     */
    List<Spot> findByPersonalIsFalseOrPersonalIsTrueAndUser(User user);

    Page<Spot> findByPersonalIsFalseOrPersonalIsTrueAndUser(User user, Pageable pageable);

    /**
     * Find all public spots.
     * @return public spots list
     */
    List<Spot> findByPersonalIsFalse();

    Page<Spot> findByPersonalIsFalse(Pageable pageable);

    /**
     * Find only personal user spots.
     * @param user spots to find
     * @return personal user spots
     */
    List<Spot> findByPersonalIsTrueAndUser(User user);

    Page<Spot> findByPersonalIsTrueAndUser(User user, Pageable pageable);
    Page<Spot> findByPersonalIsTrueAndUserId(Long userId, Pageable pageable);

}
