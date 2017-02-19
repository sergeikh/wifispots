package pet.wifispots.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import pet.wifispots.data.model.Error;

public interface ErrorRepository extends JpaRepository<Error, Long>, QueryDslPredicateExecutor<Error> {
}
