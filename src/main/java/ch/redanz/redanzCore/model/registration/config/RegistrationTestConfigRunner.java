package ch.redanz.redanzCore.model.registration.config;

import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.jobs.EODMatchingJob;
import ch.redanz.redanzCore.model.registration.jobs.EODReleaseJob;
import ch.redanz.redanzCore.model.registration.service.*;
import ch.redanz.redanzCore.model.workshop.service.*;
import freemarker.template.Configuration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
@Order(800)
@Profile("dev || test ")
public class RegistrationTestConfigRunner implements CommandLineRunner {
  private final UserService userService;
  private final BundleService bundleService;
  private final TrackService trackService;
  private final EventService eventService;
  private final DanceRoleService danceRoleService;
  private final WorkflowStatusService workflowStatusService;
  private final PersonService personService;

  private final RegistrationMatchingService registrationMatchingService;
  private final RegistrationEmailService registrationEmailService;
  private final RegistrationService registrationService;
  private final WorkflowTransitionService workflowTransitionService;
  private final DiscountService discountService;
  private final DiscountRegistrationService discountRegistrationService;
  private final FoodService foodService;
  private final SlotService slotService;
  private final FoodRegistrationService foodRegistrationService;
  private final DonationRegistrationService donationRegistrationService;

  @Autowired
  Configuration mailConfig;

  @Override
  public void run(String... args) throws Exception {
    RegistrationConfig.setup(
      personService, registrationService, bundleService, trackService, danceRoleService
      ,userService, eventService, registrationEmailService, discountService, discountRegistrationService
      ,foodService, slotService, foodRegistrationService, donationRegistrationService
    );

    RegistrationMatchingConfig.setup(
      eventService, registrationService, personService, userService, registrationMatchingService
    );

    WorkflowTransitionConfig.setup(
      eventService, registrationService, personService, userService, workflowStatusService, WorkflowStatusConfig.SUBMITTED,
      workflowTransitionService
    );

  }
}
