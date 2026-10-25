package ch.redanz.redanzCore.service.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

/*
 * Signature of the Payrexx payment webhook:
 * header X-Webhook-Signature = lowercase hex HMAC-SHA256 of the raw request body, key: signing secret of the webhook (UTF-8).
 * Without a configured secret every webhook is rejected (else anyone could confirm a payment).
 */
@Service
@Slf4j
public class WebhookSignatureService {
  public static final String SIGNATURE_HEADER = "X-Webhook-Signature";

  private final String secret;

  public WebhookSignatureService(@Value("${payrexx.webhook.secret:}") String secret) {
    this.secret = secret;
  }

  public boolean isValid(byte[] body, String signature) {
    if (secret == null || secret.isBlank()) {
      log.error("payrexx webhook rejected: no signing secret configured (payrexx.webhook.secret)");
      return false;
    }
    if (signature == null || signature.isBlank()) {
      return false;
    }
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      String expected = HexFormat.of().formatHex(mac.doFinal(body));
      // constant time comparison
      return MessageDigest.isEqual(
        expected.getBytes(StandardCharsets.UTF_8),
        signature.trim().toLowerCase().getBytes(StandardCharsets.UTF_8)
      );
    } catch (Exception exception) {
      log.error("payrexx webhook signature could not be checked", exception);
      return false;
    }
  }
}
