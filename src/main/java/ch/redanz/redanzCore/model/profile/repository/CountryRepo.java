package ch.redanz.redanzCore.model.profile.repository;


import ch.redanz.redanzCore.model.profile.entities.Country;
import ch.redanz.redanzCore.model.profile.entities.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepo extends JpaRepository<Country, Long> {
  Country findCountryById(Long personId);
  Optional<Country> findCountryBySortName(String sortName);
}
