package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import ch.redanz.redanzCore.service.payment.CreatePayment;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("app/confirming")
@AllArgsConstructor
@Slf4j
@RequestMapping(path="core-api/zahls/checkout")
public class ZahlsPaymentController {
  private final RegistrationService registrationService;

  @PostMapping("/confirm")
  public void confirmPayment(
    @RequestBody String jsonObject
  ) throws Exception {
    try {
//      userService.findByUserId(userId);
      registrationService.onPaymentConfirmed(
        JsonParser.parseString(jsonObject).getAsJsonObject()
      );
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }
}
