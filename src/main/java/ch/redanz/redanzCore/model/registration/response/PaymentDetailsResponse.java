package ch.redanz.redanzCore.model.registration.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PaymentDetailsResponse {
  private Map<String, Double> items;
  private Map<String, Double> discounts;
  private double totalAmount;
}
