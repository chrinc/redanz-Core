package ch.redanz.redanzCore.model.profile.service;

import ch.redanz.redanzCore.model.profile.Country;
import ch.redanz.redanzCore.model.profile.repository.CountryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {

  private final CountryRepo countryRepo;

  @Autowired
  public CountryService(CountryRepo countryRepo) {
    this.countryRepo = countryRepo;
  }

  public List<Country> getAllCountries() { return countryRepo.findAll(); }
  public Country findCountry(Long CountryId) { return countryRepo.findCountryById(CountryId); }
}
