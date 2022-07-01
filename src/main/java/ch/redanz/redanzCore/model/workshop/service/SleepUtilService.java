package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.SleepUtil;
import ch.redanz.redanzCore.model.workshop.repository.SleepUtilRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SleepUtilService {
  private final SleepUtilRepository sleepUtilRepository;
  public void save(SleepUtil sleepUtil) {
    sleepUtilRepository.save(sleepUtil);
  }
  public List<SleepUtil> findAll() {
    return sleepUtilRepository.findAll();
  }

  public List<SleepUtil> findHostSleepUtils() {
    List<SleepUtil> hostSleepUtils = new ArrayList<>();
    hostSleepUtils.add(sleepUtilRepository.findBySleepUtilId(1L));
    hostSleepUtils.add(sleepUtilRepository.findBySleepUtilId(2L));
    hostSleepUtils.add(sleepUtilRepository.findBySleepUtilId(3L));
    hostSleepUtils.add(sleepUtilRepository.findBySleepUtilId(4L));
    return hostSleepUtils;
  }

  public List<SleepUtil> findHosteeSleepUtils() {
    List<SleepUtil> hosteeSleepUtils = new ArrayList<>();
    hosteeSleepUtils.add(sleepUtilRepository.findBySleepUtilId(4L));
    hosteeSleepUtils.add(sleepUtilRepository.findBySleepUtilId(5L));
    return hosteeSleepUtils;
  }

  public SleepUtil findBySleepUtilId(Long sleepUtilId) {
    return sleepUtilRepository.findBySleepUtilId(sleepUtilId);
  }
}
