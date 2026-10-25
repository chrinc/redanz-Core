package ch.redanz.redanzCore.service.email;

import ch.redanz.redanzCore.model.workshop.entities.EmailAccount;
import ch.redanz.redanzCore.model.workshop.repository.EmailAccountRepo;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.angus.mail.imap.IMAPFolder;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.*;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.*;

/*
 * Email accounts (static data): sending via SMTP, storing in the "sent" folder via IMAP.
 * Passwords are stored encrypted with the master password and never returned to the frontend.
 */
@Service
@Slf4j
public class EmailAccountService {
  private static final String TIMEOUT_MS = "15000";
  // javax.mail 1.4.7 otherwise offers only old TLS versions, which Java 17 rejects
  private static final String TLS_PROTOCOLS = "TLSv1.2 TLSv1.3";
  private static final List<String> SENT_FOLDER_NAMES = List.of(
    "Sent", "INBOX.Sent", "Gesendet", "INBOX.Gesendet", "Sent Items", "Sent Messages", "Gesendete Elemente"
  );

  private final EmailAccountRepo emailAccountRepo;
  private final StandardPBEStringEncryptor encryptor;

  public EmailAccountService(
    EmailAccountRepo emailAccountRepo,
    @Value("${jasypt.encryptor.password}") String masterPassword
  ) {
    this.emailAccountRepo = emailAccountRepo;
    this.encryptor = new StandardPBEStringEncryptor();
    this.encryptor.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
    this.encryptor.setIvGenerator(new RandomIvGenerator());
    this.encryptor.setPassword(masterPassword);
  }

  /* ---------------- static data ---------------- */

  // all types (also without account) for the admin form, without password
  public List<Map<String, Object>> getAll() {
    List<Map<String, Object>> accounts = new ArrayList<>();
    for (EmailAccount.Type type : EmailAccount.Type.values()) {
      EmailAccount account = emailAccountRepo.findById(type).orElse(null);
      Map<String, Object> item = new HashMap<>();
      item.put("accountType", type.name());
      item.put("email", account == null ? "" : nullToEmpty(account.getEmail()));
      item.put("displayName", account == null ? "" : nullToEmpty(account.getDisplayName()));
      item.put("smtpHost", account == null ? "" : nullToEmpty(account.getSmtpHost()));
      item.put("smtpPort", account == null ? null : account.getSmtpPort());
      item.put("smtpSecurity", account == null || account.getSmtpSecurity() == null ? null : account.getSmtpSecurity().name());
      item.put("imapHost", account == null ? "" : nullToEmpty(account.getImapHost()));
      item.put("imapPort", account == null ? null : account.getImapPort());
      item.put("imapSecurity", account == null || account.getImapSecurity() == null ? null : account.getImapSecurity().name());
      item.put("username", account == null ? "" : nullToEmpty(account.getUsername()));
      item.put("hasPassword", account != null && account.getPasswordEncrypted() != null);
      accounts.add(item);
    }
    return accounts;
  }

  // sender options for the email form (no server data)
  public List<Map<String, String>> getSenders() {
    List<Map<String, String>> senders = new ArrayList<>();
    for (EmailAccount.Type type : EmailAccount.Type.values()) {
      if (type == EmailAccount.Type.TESTING) continue;
      EmailAccount account = emailAccountRepo.findById(type).orElse(null);
      Map<String, String> sender = new HashMap<>();
      sender.put("accountType", type.name());
      sender.put("email", account == null ? "" : nullToEmpty(account.getEmail()));
      senders.add(sender);
    }
    return senders;
  }

  public void upsert(Iterable<JsonElement> jsonArray) {
    jsonArray.forEach(element -> {
      JsonObject item = element.getAsJsonObject();
      EmailAccount.Type type = EmailAccount.Type.valueOf(item.get("accountType").getAsString());
      String email = getString(item, "email");

      // no address: no account
      if (email.isEmpty()) {
        if (emailAccountRepo.existsById(type)) {
          emailAccountRepo.deleteById(type);
        }
        return;
      }
      validateEmail(email);

      EmailAccount account = emailAccountRepo.findById(type).orElse(new EmailAccount());
      applyFields(account, type, item);
      emailAccountRepo.save(account);
    });
  }

  /* ---------------- sending ---------------- */

  // account of the type if it can send, else the main account; null = use the configuration
  public EmailAccount resolveSender(EmailAccount.Type type) {
    if (type != null) {
      EmailAccount account = emailAccountRepo.findById(type).orElse(null);
      if (account != null && account.canSend()) return account;
    }
    EmailAccount main = emailAccountRepo.findById(EmailAccount.Type.MAIN).orElse(null);
    return main != null && main.canSend() ? main : null;
  }

  public EmailAccount findMainSender() {
    return resolveSender(EmailAccount.Type.MAIN);
  }

  // recipient in test mode; null if not configured
  public String testingEmail() {
    return emailAccountRepo.findById(EmailAccount.Type.TESTING)
      .map(EmailAccount::getEmail)
      .filter(email -> !email.isBlank())
      .orElse(null);
  }

  public Session smtpSession(EmailAccount account) {
    return smtpSession(account, decrypt(account.getPasswordEncrypted()));
  }

  // stores the sent message in the "sent" folder of the account (errors are only logged)
  public void storeInSent(EmailAccount account, MimeMessage msg) {
    if (!account.canStoreSent()) {
      log.warn("email account {} has no IMAP settings, message not stored in sent folder", account.getAccountType());
      return;
    }
    storeInSent(account, decrypt(account.getPasswordEncrypted()), msg);
  }

  // with a plain password, e.g. for the configuration (fallback)
  public void storeInSent(EmailAccount account, String password, MimeMessage msg) {
    if (account.getImapHost() == null || account.getImapHost().isBlank() || account.getImapPort() == null) {
      return;
    }
    Store store = null;
    try {
      store = connectImap(account, password);
      Folder sent = findSentFolder(store);
      if (sent == null) {
        log.warn("no sent folder found for email account {}", account.getEmail());
        return;
      }
      sent.open(Folder.READ_WRITE);
      msg.setFlag(Flags.Flag.SEEN, true);
      sent.appendMessages(new Message[]{msg});
      sent.close(false);
    } catch (Exception exception) {
      log.error("storing email in sent folder failed for account " + account.getEmail(), exception);
    } finally {
      closeQuietly(store);
    }
  }

  /* ---------------- connection test ---------------- */

  // tests SMTP and IMAP with the values of the form (stored password if none is entered)
  public List<String> testConnection(JsonObject item) {
    EmailAccount.Type type = EmailAccount.Type.valueOf(item.get("accountType").getAsString());
    EmailAccount account = new EmailAccount();
    applyFields(account, type, item);
    String password = getString(item, "password");
    if (password.isEmpty()) {
      EmailAccount stored = emailAccountRepo.findById(type).orElse(null);
      password = stored == null || stored.getPasswordEncrypted() == null ? "" : decrypt(stored.getPasswordEncrypted());
    }

    List<String> results = new ArrayList<>();
    Transport transport = null;
    try {
      transport = smtpSession(account, password).getTransport("smtp");
      transport.connect(account.getSmtpHost(), account.getSmtpPort(), account.getUsername(), password);
      results.add("SMTP OK");
    } catch (Exception exception) {
      results.add("SMTP: " + exception.getMessage());
    } finally {
      closeQuietly(transport);
    }

    if (account.getImapHost() == null || account.getImapHost().isBlank() || account.getImapPort() == null) {
      results.add("IMAP: not configured");
      return results;
    }
    Store store = null;
    try {
      store = connectImap(account, password);
      Folder sent = findSentFolder(store);
      results.add(sent == null ? "IMAP OK, no sent folder found" : "IMAP OK, sent folder: " + sent.getFullName());
    } catch (Exception exception) {
      results.add("IMAP: " + exception.getMessage());
    } finally {
      closeQuietly(store);
    }
    return results;
  }

  /* ---------------- helpers ---------------- */

  private void applyFields(EmailAccount account, EmailAccount.Type type, JsonObject item) {
    account.setAccountType(type);
    account.setEmail(getString(item, "email"));
    account.setDisplayName(getString(item, "displayName"));
    account.setSmtpHost(getString(item, "smtpHost"));
    account.setSmtpPort(getInteger(item, "smtpPort"));
    account.setSmtpSecurity(getSecurity(item, "smtpSecurity", EmailAccount.Security.STARTTLS));
    account.setImapHost(getString(item, "imapHost"));
    account.setImapPort(getInteger(item, "imapPort"));
    account.setImapSecurity(getSecurity(item, "imapSecurity", EmailAccount.Security.SSL));
    account.setUsername(getString(item, "username"));
    // password only changes if a new one is entered
    String password = getString(item, "password");
    if (!password.isEmpty()) {
      account.setPasswordEncrypted(encryptor.encrypt(password));
    }
  }

  private Session smtpSession(EmailAccount account, String password) {
    Properties props = new Properties();
    props.put("mail.smtp.host", account.getSmtpHost());
    props.put("mail.smtp.port", String.valueOf(account.getSmtpPort()));
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.connectiontimeout", TIMEOUT_MS);
    props.put("mail.smtp.timeout", TIMEOUT_MS);
    props.put("mail.smtp.writetimeout", TIMEOUT_MS);
    props.put("mail.smtp.ssl.protocols", TLS_PROTOCOLS);
    if (account.getSmtpSecurity() == EmailAccount.Security.SSL) {
      props.put("mail.smtp.ssl.enable", "true");
    } else if (account.getSmtpSecurity() != EmailAccount.Security.NONE) {
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.starttls.required", "true");
    }
    String username = account.getUsername();
    return Session.getInstance(props, new Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });
  }

  private Store connectImap(EmailAccount account, String password) throws MessagingException {
    boolean ssl = account.getImapSecurity() == null || account.getImapSecurity() == EmailAccount.Security.SSL;
    String protocol = ssl ? "imaps" : "imap";
    Properties props = new Properties();
    props.put("mail." + protocol + ".connectiontimeout", TIMEOUT_MS);
    props.put("mail." + protocol + ".timeout", TIMEOUT_MS);
    props.put("mail." + protocol + ".ssl.protocols", TLS_PROTOCOLS);
    if (account.getImapSecurity() == EmailAccount.Security.STARTTLS) {
      props.put("mail.imap.starttls.enable", "true");
      props.put("mail.imap.starttls.required", "true");
    }
    Store store = Session.getInstance(props).getStore(protocol);
    store.connect(account.getImapHost(), account.getImapPort(), account.getUsername(), password);
    return store;
  }

  // folder marked as "\Sent" (IMAP special use), else a common name
  private Folder findSentFolder(Store store) throws MessagingException {
    for (Folder folder : store.getDefaultFolder().list("*")) {
      if (folder instanceof IMAPFolder && Arrays.asList(((IMAPFolder) folder).getAttributes()).contains("\\Sent")) {
        return folder;
      }
    }
    for (String name : SENT_FOLDER_NAMES) {
      Folder folder = store.getFolder(name);
      if (folder.exists()) return folder;
    }
    return null;
  }

  private String decrypt(String encrypted) {
    return encrypted == null ? "" : encryptor.decrypt(encrypted);
  }

  private static void validateEmail(String email) {
    try {
      new InternetAddress(email, true).validate();
    } catch (AddressException addressException) {
      throw new ApiRequestException("Invalid email: " + email);
    }
  }

  private static String getString(JsonObject item, String key) {
    return item.get(key) == null || item.get(key).isJsonNull() ? "" : item.get(key).getAsString().trim();
  }

  private static Integer getInteger(JsonObject item, String key) {
    String value = getString(item, key);
    return value.isEmpty() ? null : Integer.valueOf(value);
  }

  private static EmailAccount.Security getSecurity(JsonObject item, String key, EmailAccount.Security defaultValue) {
    String value = getString(item, key);
    return value.isEmpty() ? defaultValue : EmailAccount.Security.valueOf(value);
  }

  private static String nullToEmpty(String value) {
    return value == null ? "" : value;
  }

  private static void closeQuietly(jakarta.mail.Service service) {
    try {
      if (service != null && service.isConnected()) service.close();
    } catch (MessagingException ignored) {
    }
  }
}
