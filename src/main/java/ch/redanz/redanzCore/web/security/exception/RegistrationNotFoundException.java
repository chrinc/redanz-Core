package ch.redanz.redanzCore.web.security.exception;

public class RegistrationNotFoundException extends RuntimeException {
  public RegistrationNotFoundException(String message) {
    super(message);
  }
}
