package ch.redanz.redanzCore.web.security.exception;

public class UserNotFoundException extends RuntimeException{
  public UserNotFoundException(String message) {
      super(message);
  }
}
