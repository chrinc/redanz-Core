package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.registration.jobs.EODCancelJob;
import ch.redanz.redanzCore.model.registration.jobs.EODMatchingJob;
import ch.redanz.redanzCore.model.registration.jobs.EODReleaseJob;
import ch.redanz.redanzCore.model.registration.jobs.EODReminderJob;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("core-api/app/jobs")
public class JobController {

  private final EODCancelJob eodCancelJob;
  private final EODMatchingJob eodMatchingJob;
  private final EODReleaseJob eodReleaseJob;
  private final EODReminderJob eodReminderJob;

  @GetMapping(path = "/run-cancel")
  public void runCancel() {
    try {
      eodCancelJob.runCancelJob();
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/run-matching")
  public void runMatching() {
    try {
      eodMatchingJob.runMatching();
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/run-release")
  public void runRelease() {
    try {
      eodReleaseJob.runRelease();
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/run-reminder")
  public void runReminder() {
    try {
      eodReminderJob.runReminderJob();
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }
}
