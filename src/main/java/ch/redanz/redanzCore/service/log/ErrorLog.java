package ch.redanz.redanzCore.service.log;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Length;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "error_log")
public class ErrorLog {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "log_id")
  private Long registrationId;
  private LocalDateTime timestamp;
  private String errorType;

  // longtext as before Hibernate 6 (else tinytext with max. 255 characters)
  @Lob
  @Column(length = Length.LONG32)
  private String error;

  public ErrorLog(LocalDateTime timestamp, String errorType, String error) {
    this.timestamp = timestamp;
    this.errorType = errorType;
    this.error = error;
  }
}
