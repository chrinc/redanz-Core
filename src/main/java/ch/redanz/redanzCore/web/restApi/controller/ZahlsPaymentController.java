package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import ch.redanz.redanzCore.service.payment.CreatePayment;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("app/confirming")
@AllArgsConstructor
@Slf4j
@RequestMapping(path="core-api/zahls/checkout")
public class ZahlsPaymentController {
  @PostMapping("/confirm")
  public void confirmPayment(
    @RequestBody String webhook
  ) throws Exception {
    log.info("inc, webhook: {}", webhook);
    try {
//      userService.findByUserId(userId);
//      registrationService.onPaymentReceived(userId);

    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }
}
