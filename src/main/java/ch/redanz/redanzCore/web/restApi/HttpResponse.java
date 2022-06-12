package ch.redanz.redanzCore.web.restApi;

import org.springframework.http.HttpStatus;

public class HttpResponse {
  private int httpStatusCode;
  private HttpStatus httpStatus;
  private String reason;
  private String message;

//  {
//    code: 200,
//    httpStatus: "OK",
//    reason: "ok",
//    message: "your request was successful"
//  }

  public HttpResponse() {}

  public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {
    this.httpStatusCode = httpStatusCode;
    this.httpStatus = httpStatus;
    this.reason = reason;
    this.message = message;
  }

  public int getHttpStatusCode() {
    return httpStatusCode;
  }

  public void setHttpStatusCode(int httpStatusCode) {
    this.httpStatusCode = httpStatusCode;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  public void setHttpStatus(HttpStatus httpStatus) {
    this.httpStatus = httpStatus;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
