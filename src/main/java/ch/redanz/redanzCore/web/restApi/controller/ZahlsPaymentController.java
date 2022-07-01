package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.registration.response.PaymentDetailsResponse;
import ch.redanz.redanzCore.model.registration.service.PaymentService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeoutException;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(path="core-api/zahls")
public class ZahlsPaymentController {
  private final RegistrationService registrationService;
  private final PaymentService paymentService;
  private final EventService eventService;

  @GetMapping("/payment-intent")
  public PaymentDetailsResponse getPaymentIntent(
    @RequestParam Long userId
  ) {
    try {
      return paymentService.getPaymentDetails(
        registrationService.getRegistration(userId, eventService.getCurrentEvent())
      );
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }
  @GetMapping("/payment-confirmation")
  public ResponseEntity<Boolean> getPaymentConfirmation(
    @RequestParam Long userId
  ) {
    try {
      return
        ResponseEntity.ok().body(paymentService.awaitPaymentConfirmation(
        registrationService.getRegistration(userId, eventService.getCurrentEvent())
        )
      );
    } catch (TimeoutException timeoutException) {
        throw new ApiRequestException(OutTextConfig.LABEL_ERROR_TIMEOUT_EN.getOutTextKey(), HttpStatus.REQUEST_TIMEOUT);
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
