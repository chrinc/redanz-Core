package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.registration.entities.PaymentStatus;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationType;
import ch.redanz.redanzCore.model.registration.response.PaymentDetailsResponse;
import ch.redanz.redanzCore.model.registration.service.PaymentService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.service.payment.WebhookSignatureService;
import ch.redanz.redanzCore.service.log.ErrorLogService;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(path = "core-api/payrexx")
public class PayrexxPaymentController {
  private final RegistrationService registrationService;
  private final PaymentService paymentService;
  private final ErrorLogService errorLogService;
  private final WebhookSignatureService webhookSignatureService;


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
      registrationService.updateSoldOut(registration.getEvent());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/reset-pending")
  @Transactional
  public void resetPending(
    @RequestParam("registrationId") Long registrationId
  ) {
    try {
     Registration registration = registrationService.findByRegistrationId(registrationId);
      paymentService.updateRegistrationPaymentStatus(registration, PaymentStatus.PENDING);
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/payment-status")
  @Transactional
  public ResponseEntity<PaymentStatus>  getPaymentStatus(
    @RequestParam Long registrationId
  ) {
    Registration registration = registrationService.findByRegistrationId(registrationId);
   return ResponseEntity.ok(registration.getPaymentStatus());
  }

  // webhook of Payrexx: only accepted with a valid signature (raw body, not re-serialized)
  @PostMapping("/checkout/confirm")
  public void confirmPayment(
    @RequestBody byte[] body,
    @RequestHeader(value = WebhookSignatureService.SIGNATURE_HEADER, required = false) String signature
  ) {
    if (!webhookSignatureService.isValid(body, signature)) {
      errorLogService.addLog("/confirmPayment", "invalid webhook signature");
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNAUTHORIZED_EN.getOutTextKey(), HttpStatus.UNAUTHORIZED);
    }
    try {
      JsonObject request = JsonParser.parseString(new String(body, StandardCharsets.UTF_8)).getAsJsonObject();
      JsonObject transaction = request.get("transaction").getAsJsonObject();
      Long registrationId = transaction.get("referenceId").getAsLong();
      Registration registration = registrationService.findByRegistrationId(registrationId);
      paymentService.onPaymentConfirmed(
        registration,
        transaction.get("amount").getAsLong() / 100,
        transaction.get("status").getAsString()
      );
      registrationService.updateSoldOut(registration.getEvent());
    } catch (Exception exception) {
      errorLogService.addLog("/confirmPayment", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }
}
