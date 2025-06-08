package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationType;
import ch.redanz.redanzCore.model.registration.response.PaymentDetailsResponse;
import ch.redanz.redanzCore.model.registration.service.PaymentService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeoutException;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(path = "core-api/payrexx")
public class PayrexxPaymentController {
  private final RegistrationService registrationService;
  private final PaymentService paymentService;
  private final EventService eventService;

  @GetMapping("/payment-intent")
  public PaymentDetailsResponse getPaymentIntent(
    @RequestParam("registrationId") Long registrationId
  ) {
    try {

      return paymentService.getPaymentDetails(
        registrationService.findByRegistrationId(registrationId)
      );
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping("/payment-confirmation")
  public ResponseEntity<Boolean> getPaymentConfirmation(
    @RequestParam Long userId,
    @RequestParam Long registrationId
  ) {
    try {
      return
        ResponseEntity.ok().body(paymentService.awaitPaymentConfirmation(
            registrationService.getRegistration(userId, registrationService.findByRegistrationId(registrationId).getEvent(), RegistrationType.PARTICIPANT)
          )
        );
    } catch (TimeoutException timeoutException) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_TIMEOUT_EN.getOutTextKey(), HttpStatus.REQUEST_TIMEOUT);
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/manual-pay")
  @Transactional
  public void manualPay(
    @RequestParam("registrationId") Long registrationId
  ) {
    try {
      Registration registration = registrationService.findByRegistrationId(registrationId);
      paymentService.onPaymentReceived(registration, paymentService.amountDue(registration));
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @PostMapping("/checkout/confirm")
  public void confirmPayment(
    @RequestBody String jsonObject
  ) {
    try {
      paymentService.onPaymentConfirmed(
        JsonParser.parseString(jsonObject).getAsJsonObject()
      );
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }
}
