package ch.redanz.redanzCore.service.log;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class ErrorLogService {
  private final ErrorLogRepo errorLogRepo;

  // own transaction: the log is kept when the transaction of the request is rolled back
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void addLog(String type, String log) {
    ErrorLog errorLog = new ErrorLog(
      LocalDateTime.now(),
      type,
      log
    );
    errorLogRepo.save(errorLog);
  }
}
