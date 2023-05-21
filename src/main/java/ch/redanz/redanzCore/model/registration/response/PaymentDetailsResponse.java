package ch.redanz.redanzCore.model.registration.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PaymentDetailsResponse {
  private List<List<String>> items;
  private List<List<String>> specials;
  private List<List<String>> privateClasses;
  private List<List<String>> foodSlots;
  private List<List<String>> donation;
  private List<List<String>> discounts;
  private long totalAmount;
  private long amountPaid;
  private long amountDue;
  private long registrationId;
  private boolean isConfirming;
}
