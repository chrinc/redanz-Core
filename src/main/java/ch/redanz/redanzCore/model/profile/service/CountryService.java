package ch.redanz.redanzCore.model.profile.service;

import ch.redanz.redanzCore.model.profile.entities.Country;
import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.profile.repository.CountryRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CountryService {

  private final CountryRepo countryRepo;

  public void save(Country country) {
    countryRepo.save(country);
  }

  public boolean countryExists(String sortName) {
    return countryRepo.findCountryBySortName(sortName).isPresent();
  }
  public Country findCountry(String sortName) {
    return countryRepo.findCountryBySortName(sortName).get();
  }
  public List<Country> getAllCountries() { return countryRepo.findAll(); }
  public Country findCountry(Long CountryId) { return countryRepo.findCountryById(CountryId); }
}
