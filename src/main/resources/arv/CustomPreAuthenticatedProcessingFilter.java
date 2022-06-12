//package ch.redanz.redanzCore.security;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
//
//import javax.servlet.http.HttpServletRequest;
//
//@Slf4j
//public class CustomPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {
//  @Override
//  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
//    log.info("inc, request 1: {}", request);
//    return null;
//  }
//
//  @Override
//  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
//    log.info("inc, request 2:  {}", request);
//    return null;
//  }
//}
