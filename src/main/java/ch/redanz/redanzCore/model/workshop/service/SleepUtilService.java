package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.Food;
import ch.redanz.redanzCore.model.workshop.SleepUtil;
import ch.redanz.redanzCore.model.workshop.repository.SleepUtilRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SleepUtilService {
  private final SleepUtilRepository sleepUtilRepository;

  public List<SleepUtil> findAll() {
    return sleepUtilRepository.findAll();
  }
  public List<SleepUtil> findHostSleepUtils() {
    List<SleepUtil> hostSleepUtils = new ArrayList<>();

    hostSleepUtils.add(sleepUtilRepository.findBySleepUtilId(Long.valueOf(1)));
    hostSleepUtils.add(sleepUtilRepository.findBySleepUtilId(Long.valueOf(2)));
    hostSleepUtils.add(sleepUtilRepository.findBySleepUtilId(Long.valueOf(3)));
    hostSleepUtils.add(sleepUtilRepository.findBySleepUtilId(Long.valueOf(4)));
    return hostSleepUtils;
  }

  public List<SleepUtil> findHosteeSleepUtils() {
    List<SleepUtil> hosteeSleepUtils = new ArrayList<>();
    hosteeSleepUtils.add(sleepUtilRepository.findBySleepUtilId(Long.valueOf(4)));
    hosteeSleepUtils.add(sleepUtilRepository.findBySleepUtilId(Long.valueOf(5)));
    return hosteeSleepUtils;
  }

  public SleepUtil findBySleepUtilId(Long sleepUtilId) {
    return sleepUtilRepository.findBySleepUtilId(sleepUtilId);
  }
}
