package ch.redanz.redanzCore.model.profile.config;

import ch.redanz.redanzCore.model.profile.entities.FieldProperty;
import ch.redanz.redanzCore.model.profile.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
@Order(700)
// Runs in all Profiles
public class ProfileConfigRunner implements CommandLineRunner {
  private final FieldPropertyService fieldPropertyService;

  @Override
  public void run(String... args) {
    FieldPropertyConfig.setup(fieldPropertyService);
  }
}
