package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.registration.service.PaymentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j

@RestController
@AllArgsConstructor
@RequestMapping(path = "core-api/stripe/checkout")
public class StripePaymentController {

  private final PaymentService paymentService;

//  @PostMapping("/create-payment-intent")
//  public CreatePaymentResponse createPaymentIntent(
//    @RequestBody CreatePayment createPayment
//  ) throws StripeException {
//    PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
//      .setCurrency("chf")
//      .setAmount(15 * 100L)  // createPaymentent
//      .build();
//
//    // Create a PaymentIntent with the order amount and currency
//    PaymentIntent paymentIntent = PaymentIntent.create(createParams);
//    return new CreatePaymentResponse(paymentIntent.getClientSecret());
//  }

//  @PostMapping("/confirm")
//  public void confirmPayment(
//    @RequestParam("userId") Long userId
//  ) throws Exception {
//    log.info("inc, received user: {}", userId);
//    try {
//      paymentService.onPaymentReceived(userId);
//    } catch (Exception exception) {
//      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
//    }
//  }
}


