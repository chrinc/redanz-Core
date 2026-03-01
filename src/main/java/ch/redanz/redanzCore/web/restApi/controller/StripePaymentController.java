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
}


