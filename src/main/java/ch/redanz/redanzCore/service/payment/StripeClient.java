package ch.redanz.redanzCore.service.payment;

import com.stripe.Stripe;
import com.stripe.model.Charge;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class StripeClient {
//  @Autowire
//  private Gson gson;

  @Autowired
  StripeClient() {
    Stripe.apiKey = "sk_test_51L6yoUDLSlbx9VTuKivVoJLYpHVDFepccRhxwdANFgZFmAWOdJ4L2ep6To3C9fa6lyOajOSAxhYSEyCxumz441NT00qm85MKp7";
  }
//  StripeClient() {
//    Stripe.apiKey = "${stripe.apikey}";
//  }

  public Charge chargeCreditCard(String token, double amount) throws Exception {
    Map<String, Object> chargeParams = new HashMap<String, Object>();
    chargeParams.put("amount", (int) (amount * 100));
    chargeParams.put("currency", "USD");
    chargeParams.put("source", token);
    Charge charge = Charge.create(chargeParams);
    return charge;
  }
}
