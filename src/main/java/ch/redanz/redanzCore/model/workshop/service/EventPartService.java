package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.config.EventPartConfig;
import ch.redanz.redanzCore.model.workshop.configTest.EventPartInfoConfig;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventPart;
import ch.redanz.redanzCore.model.workshop.entities.EventPartInfo;
import ch.redanz.redanzCore.model.workshop.repository.EventPartInfoRepo;
import ch.redanz.redanzCore.model.workshop.repository.EventPartRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class EventPartService {
  private final EventPartRepo eventPartRepo;
  private final EventPartInfoRepo eventPartInfoRepo;
  private final OutTextService outTextService;


  public void save(EventPart eventPart) {
    eventPartRepo.save(eventPart);
  }
   public EventPart findByKey(String key) {
    return eventPartRepo.findByEventPartKey(key);
  }

  public void save (EventPartInfo eventPartInfo) {
    eventPartInfoRepo.save(eventPartInfo);
  }
  public EventPartInfo findByEventAndEventPart(Event event, EventPart eventPart) {
    return eventPartInfoRepo.findByEventAndEventPart(event, eventPart);
  }


  public List<Map<String, String>> getEventPartInfoData(EventPartInfo eventPartInfo) {
    List<Map<String, String>> partInfoData = new ArrayList<>();
    partInfoData.add(eventPartInfo.dataMap());
    return partInfoData;
  }

  public void hideSchemaItem(List<Map<String, String>> schema, String key) {
    schema.stream()
      .filter(map -> key.equals(map.get("key")))
      .forEach(map -> map.put("hide", "true"));
  }
  public boolean existsByKey(String eventPartKey) {
    return eventPartRepo.existsByEventPartKey(eventPartKey);
  }

  public String terms(Event event) {
    return eventPartInfoRepo.findByEventAndEventPart(event, eventPartRepo.findByEventPartKey(EventPartConfig.TERMS.getEventPartKey())).getHintLink();
  }
  public List<Map<String, String>> getEventPartInfoSchema(EventPartInfo eventPartInfo) {
    List<Map<String, String>> eventPartInfoSchema = EventPartInfo.schema();
    eventPartInfoSchema.stream()
      .filter(map -> "title".equals(map.get("type")) && "plural".equals(map.get("key")))
      .forEach(map -> map.put("label", eventPartInfo.getEventPart().getName()));

    if (!eventPartInfo.isTitleActive()) {hideSchemaItem(eventPartInfoSchema, "title");}
    if (!eventPartInfo.isTitleExistActive()) {hideSchemaItem(eventPartInfoSchema, "titleExist");}
    if (!eventPartInfo.isInvalidActive()) {hideSchemaItem(eventPartInfoSchema, "invalid");}
    if (!eventPartInfo.isSubtitleActive()) {hideSchemaItem(eventPartInfoSchema, "subtitle");}
    if (!eventPartInfo.isSubtitleLinkActive()) {hideSchemaItem(eventPartInfoSchema, "subtitleLink");}
    if (!eventPartInfo.isHintActive()) {hideSchemaItem(eventPartInfoSchema, "hint");}
    if (!eventPartInfo.isHintLinkActive()) {hideSchemaItem(eventPartInfoSchema, "hintLink");}
    if (!eventPartInfo.isHint2Active()) {hideSchemaItem(eventPartInfoSchema, "hint2");}
    if (!eventPartInfo.isHint2LinkActive()) {hideSchemaItem(eventPartInfoSchema, "hint2Link");}

    return eventPartInfoSchema;
  }

  public void newBaseEventPartInfo(Event newEvent) {
    for (EventPartInfoConfig eventPartInfoConfig : EventPartInfoConfig.values()) {
      EventPart eventPart = findByKey(eventPartInfoConfig.eventPartConfig.getEventPartKey());
      save(
        new EventPartInfo(
          newEvent,
          eventPart,
          eventPartInfoConfig.getTitleRequired(),
          eventPartInfoConfig.getTitleExistRequired(),
          eventPartInfoConfig.getInvalidRequired(),
          eventPartInfoConfig.getSubtitleRequired(),
          eventPartInfoConfig.getSubtitleLinkRequired(),
          eventPartInfoConfig.getHintRequired(),
          eventPartInfoConfig.getHintLinkRequired(),
          eventPartInfoConfig.getHint2Required(),
          eventPartInfoConfig.getHint2LinkRequired()
        )
      );
    }
  }

  public void clone(Event baseEvent, Event newEvent) {
//    log.info("cloen event part info");
    eventPartInfoRepo.findAllByEvent(baseEvent).forEach(eventPartInfo -> {
      save(new EventPartInfo(
        newEvent,
        eventPartInfo.getEventPart(),
        outTextService.clone(eventPartInfo.getTitle()),
        eventPartInfo.isTitleActive(),
        eventPartInfo.getTitleExist(),
        eventPartInfo.isTitleExistActive(),
        eventPartInfo.getInvalid(),
        eventPartInfo.isInvalidActive(),
        outTextService.clone(eventPartInfo.getSubtitle()),
        eventPartInfo.isSubtitleActive(),
        outTextService.clone(eventPartInfo.getSubtitleLink()),
        eventPartInfo.isSubtitleLinkActive(),
        outTextService.clone(eventPartInfo.getHint()),
        eventPartInfo.isHintActive(),
        outTextService.clone(eventPartInfo.getHintLink()),
        eventPartInfo.isHintLinkActive(),
        outTextService.clone(eventPartInfo.getHint2()),
        eventPartInfo.isHint2Active(),
        outTextService.clone(eventPartInfo.getHint2Link()),
        eventPartInfo.isHint2LinkActive()
      ));
    });
  }
}
