package ch.redanz.redanzCore.service.log;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class ErrorLogService {
  private final ErrorLogRepo errorLogRepo;

  public void addLog(String type, String log) {
    ErrorLog errorLog = new ErrorLog(
      LocalDateTime.now(),
      type,
      log
    );
    errorLogRepo.save(errorLog);
  }
}
