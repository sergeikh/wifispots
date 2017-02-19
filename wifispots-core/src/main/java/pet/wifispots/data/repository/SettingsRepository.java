package pet.wifispots.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import pet.wifispots.data.model.settings.Settings;

public interface SettingsRepository extends JpaRepository<Settings, Long>, QueryDslPredicateExecutor<Settings>{

}
