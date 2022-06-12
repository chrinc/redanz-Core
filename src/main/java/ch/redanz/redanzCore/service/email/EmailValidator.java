package ch.redanz.redanzCore.service.email;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class EmailValidator implements Predicate<String> {

  @Override
  public boolean test(String  s) {
    // @todo: add regex to validate ch.redanz.redanzCore.email
    return true;
  }
}
