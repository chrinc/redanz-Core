package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.registration.entities.PaymentStatus;
import ch.redanz.redanzCore.model.registration.entities.PaymentType;
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
import java.util.List;
import java.util.Map;
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

  // payment entered by an organizer (e.g. cash), body: {amount, comment: {EN, GE}}
  @PostMapping(path = "/manual-payment")
  @Transactional
  public void manualPayment(
    @RequestParam("registrationId") Long registrationId,
    @RequestBody String jsonObject
  ) {
    addManualBooking(registrationId, PaymentType.PAYMENT, jsonObject);
    try {
      Registration registration = registrationService.findByRegistrationId(registrationId);
      paymentService.onPaymentUpdated(registration);
      registrationService.updateSoldOut(registration.getEvent());
    } catch (Exception exception) {
      errorLogService.addLog("manual-payment", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  // refund to the participant, body: {amount, comment: {EN, GE}}
  @PostMapping(path = "/manual-refund")
  @Transactional
  public void manualRefund(
    @RequestParam("registrationId") Long registrationId,
    @RequestBody String jsonObject
  ) {
    addManualBooking(registrationId, PaymentType.REFUND, jsonObject);
  }

  // fee charged in addition to the booked items (e.g. cancellation fee), body: {amount, comment: {EN, GE}}
  @PostMapping(path = "/manual-fee")
  @Transactional
  public void manualFee(
    @RequestParam("registrationId") Long registrationId,
    @RequestBody String jsonObject
  ) {
    addManualBooking(registrationId, PaymentType.FEE, jsonObject);
  }

  // payments, refunds and fees of the registration
  @GetMapping(path = "/bookings")
  @Transactional
  public List<Map<String, Object>> bookings(
    @RequestParam("registrationId") Long registrationId
  ) {
    try {
      return paymentService.bookings(registrationService.findByRegistrationId(registrationId));
    } catch (Exception exception) {
      errorLogService.addLog("bookings", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @PostMapping(path = "/booking/delete")
  @Transactional
  public void deleteBooking(
    @RequestParam("registrationId") Long registrationId,
    @RequestParam("registrationPaymentId") Long registrationPaymentId
  ) {
    try {
      paymentService.deleteBooking(registrationService.findByRegistrationId(registrationId), registrationPaymentId);
    } catch (Exception exception) {
      errorLogService.addLog("booking/delete", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  // change type, amount or comment of a payment, refund or fee, body: {type, amount, comment: {EN, GE}}
  @PostMapping(path = "/booking/update")
  @Transactional
  public void updateBooking(
    @RequestParam("registrationId") Long registrationId,
    @RequestParam("registrationPaymentId") Long registrationPaymentId,
    @RequestBody String jsonObject
  ) {
    JsonObject request = parseBooking(jsonObject);
    PaymentType type;
    try {
      type = PaymentType.valueOf(request.get("type").getAsString());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
    try {
      Registration registration = registrationService.findByRegistrationId(registrationId);
      paymentService.updateBooking(registration, registrationPaymentId, type, request.get("amount").getAsLong(), comment(request));
      if (type == PaymentType.PAYMENT) {
        paymentService.onPaymentUpdated(registration);
        registrationService.updateSoldOut(registration.getEvent());
      }
    } catch (Exception exception) {
      errorLogService.addLog("booking/update", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  private void addManualBooking(Long registrationId, PaymentType type, String jsonObject) {
    JsonObject request = parseBooking(jsonObject);
    try {
      paymentService.addBooking(
        registrationService.findByRegistrationId(registrationId),
        type,
        request.get("amount").getAsLong(),
        comment(request)
      );
    } catch (Exception exception) {
      errorLogService.addLog("manual-" + type.name().toLowerCase(), exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  // booking request with an amount > 0 (whole CHF)
  private JsonObject parseBooking(String jsonObject) {
    try {
      JsonObject request = JsonParser.parseString(jsonObject).getAsJsonObject();
      if (request.get("amount").getAsLong() > 0) {
        return request;
      }
    } catch (Exception ignored) {
      // invalid request
    }
    throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
  }

  private static JsonObject comment(JsonObject request) {
    return request.has("comment") && request.get("comment").isJsonObject() ? request.getAsJsonObject("comment") : null;
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

      // payments not made by a registration (e.g. QR codes of the bar): no registration id as reference, ignored
      String referenceId = transaction.has("referenceId") && !transaction.get("referenceId").isJsonNull()
        ? transaction.get("referenceId").getAsString().trim()
        : "";
      if (!referenceId.matches("\\d+")) {
        return;
      }
      Long registrationId = Long.valueOf(referenceId);
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
