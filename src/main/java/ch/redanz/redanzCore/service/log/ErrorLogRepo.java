package ch.redanz.redanzCore.service.log;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorLogRepo extends JpaRepository<ErrorLog, Long> {
}
