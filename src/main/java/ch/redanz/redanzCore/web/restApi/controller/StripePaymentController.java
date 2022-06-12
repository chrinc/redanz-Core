package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.workshop.config.EventConfig;
import ch.redanz.redanzCore.model.registration.config.WorkflowStatusConfig;
import ch.redanz.redanzCore.model.registration.service.*;
import ch.redanz.redanzCore.model.registration.WorkflowTransition;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.service.payment.CreatePayment;
import ch.redanz.redanzCore.service.payment.CreatePaymentResponse;
import com.stripe.exception.StripeException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import java.time.LocalDateTime;

@Slf4j

@RestController
//@RequestMapping("app/confirming")
@AllArgsConstructor
@RequestMapping(path="core-api/stripe/checkout")
  public class StripePaymentController {

//  private final StripeClient stripeClient;
  private final UserService userService;
  private final WorkflowTransitionService workflowTransitionService;
  private final WorkflowStatusService workflowStatusService;
  private final RegistrationService registrationService;
  private final PersonService personService;
  private final EventService eventService;

//  @Autowired
//  StripePaymentController(StripeClient stripeClient) {
//    this.stripeClient = stripeClient;
//  }

//  public Charge chargeCard(HttpServletRequest request) throws Exception {
//    log.info("inc, make payment? {}", request);
//    String token = request.getHeader("token");
//    Double amount = Double.parseDouble(request.getHeader("amount"));
//    return this.stripeClient.chargeCreditCard(token, amount);
//  }
  @PostMapping("/create-payment-intent")
  public CreatePaymentResponse createPaymentIntent(@RequestBody CreatePayment createPayment) throws StripeException {
    log.info("inc, got create payment? {}", createPayment);
    PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
        .setCurrency("chf")
        .setAmount(15 * 100L)  // createPaymentent
        .build();

      log.info("inc, got create createParams? {}", createParams);
      // Create a PaymentIntent with the order amount and currency
      PaymentIntent paymentIntent = PaymentIntent.create(createParams);
      log.info("inc, got create response? {}");
      return new CreatePaymentResponse(paymentIntent.getClientSecret());
  }

  @PostMapping("/confirm")
  public void confirmPayment(
    @RequestParam("userId") Long userId
  ) throws Exception {
    log.info("inc, received user: {}", userId);
    userService.findByUserId(userId);

    workflowTransitionService.saveWorkflowTransition(
        new WorkflowTransition(
          workflowStatusService.findByWorkflowStatusName(WorkflowStatusConfig.DONE.getName()),
          registrationService.findByParticipantAndEvent(
                  personService.findByUser(userService.findByUserId(userId)),
                  eventService.findByName(EventConfig.EVENT2022.getName())
          ).get(),
          LocalDateTime.now()

      )
    );
  }
}



//}


