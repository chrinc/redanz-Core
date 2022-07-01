package ch.redanz.redanzCore.model.profile.repository;


import ch.redanz.redanzCore.model.profile.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepo extends JpaRepository<Country, Long> {
  Country findCountryById(Long personId);
}
