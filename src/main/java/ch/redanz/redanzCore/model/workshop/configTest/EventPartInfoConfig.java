package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.config.EventPartConfig;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventPart;
import ch.redanz.redanzCore.model.workshop.entities.EventPartInfo;
import ch.redanz.redanzCore.model.workshop.service.EventPartService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum EventPartInfoConfig {
  EVENT_INFO(           EventConfig.REDANZ_EVENT, EventPartConfig.EVENT           , OutTextConfig.LABEL_EVENT_TITLE_EN.getOutTextKey()                      ,true , OutTextConfig.LABEL_EVENT_TITLE_EN.getOutTextKey()           , true , null, false, null,false, null,false, null, false,null , false,null, false,null, false),
  BUNDLE_INFO(          EventConfig.REDANZ_EVENT, EventPartConfig.BUNDLE          , OutTextConfig.LABEL_BUNDLE_TITLE_EN.getOutTextKey()                     ,true , OutTextConfig.LABEL_BUNDLE_TITLE_EXIST_EN.getOutTextKey()    , true ,OutTextConfig.LABEL_BUNDLE_INVALID_EN.getOutTextKey(),true , OutTextConfig.LABEL_BUNDLE_SUBTITLE_EN.getOutTextKey(), true,OutTextConfig.LABEL_BUNDLE_SUBTITLE_LINK_EN.getOutTextKey(), true,null, false,null, false,null, false,null, false),
  TRACK_INFO(           EventConfig.REDANZ_EVENT, EventPartConfig.TRACK           , OutTextConfig.LABEL_TRACK_TITLE_EN.getOutTextKey()                     , true , OutTextConfig.LABEL_TRACK_TITLE_EXIST_EN.getOutTextKey()     , true ,OutTextConfig.LABEL_TRACK_INVALID_EN.getOutTextKey() ,true , OutTextConfig.LABEL_TRACK_SUBTITLE_EN.getOutTextKey() , true,OutTextConfig.LABEL_TRACK_SUBTITLE_LINK_EN.getOutTextKey() , true,null, false,null, false,null, false,null, false),
  DISCOUNT_INFO(        EventConfig.REDANZ_EVENT, EventPartConfig.DISCOUNT        , OutTextConfig.LABEL_DISCOUNT_TITLE_EN.getOutTextKey()                  , true , OutTextConfig.LABEL_DISCOUNT_TITLE_EXIST_EN.getOutTextKey()  , true, null, false,null,false,null,false,null, false,null, false,null, false,null, false),
  SPECIAL_INFO(         EventConfig.REDANZ_EVENT, EventPartConfig.SPECIAL         , OutTextConfig.LABEL_SPECIAL_TITLE_EN.getOutTextKey()                   , true , OutTextConfig.LABEL_SPECIAL_TITLE_EXIST_EN.getOutTextKey()   , true, null, false, null, false, null, false, null, false, null, false, null, false, null, false),
  FOOD_INFO(            EventConfig.REDANZ_EVENT, EventPartConfig.FOOD            , OutTextConfig.LABEL_MEAL_TITLE_EN.getOutTextKey()                      , true , OutTextConfig.LABEL_MEAL_TITLE_EXIST_EN.getOutTextKey()      , true, null, false, null, false, null, false, null, false, null, false, null, false, null, false),
  PRIVATE_INFO(         EventConfig.REDANZ_EVENT, EventPartConfig.PRIVATE         , OutTextConfig.LABEL_PRIVATES_TITLE_EN.getOutTextKey()                  , true , OutTextConfig.LABEL_PRIVATES_TITLE_EXIST_EN.getOutTextKey()  , true, null, false, null, false, null, false, OutTextConfig.LABEL_PRIVATES_HINT_EN.getOutTextKey(), true, null, false, null, false, null, false),
  DANCE_ROLE(           EventConfig.REDANZ_EVENT, EventPartConfig.DANCE_ROLE      , OutTextConfig.LABEL_DANCE_ROLE_TITLE_EN.getOutTextKey()                , true ,OutTextConfig.LABEL_DANCE_ROLE_TITLE_EXIST_EN.getOutTextKey(), true, null, false, null, false, null, false, null, false, null, false, null, false, null, false),
  PARTNER_REGISTRATION( EventConfig.REDANZ_EVENT, EventPartConfig.PARTNER_REGISTRATION , OutTextConfig.LABEL_PARTNER_REGISTRATION_TITLE_EN.getOutTextKey() , true ,OutTextConfig.LABEL_PARTNER_REGISTRATION_TITLE_EXIST_EN.getOutTextKey(), true, null, false, null, false, null, false, null, false, null, false, null, false, null, false),
  PARTNER_EMAIL(        EventConfig.REDANZ_EVENT, EventPartConfig.PARTNER_EMAIL   , OutTextConfig.LABEL_PARTNER_EMAIL_TITLE_EN.getOutTextKey()             , true ,OutTextConfig.LABEL_PARTNER_EMAIL_TITLE_EXIST_EN.getOutTextKey(), true, null, false, null, false, null, false, OutTextConfig.LABEL_PARTNER_EMAIL_HINT_EN.getOutTextKey(), true, null, false, null, false, null, false),



  // Acccommodation
  ACCOMMODATION_INFO(   EventConfig.REDANZ_EVENT, EventPartConfig.ACCOMMODATION        , OutTextConfig.LABEL_ACCOMMODATION_TITLE_EN.getOutTextKey()        , true ,OutTextConfig.LABEL_ACCOMMODATION_TITLE_EXIST_EN.getOutTextKey(), true, OutTextConfig.LABEL_ACCOMMODATION_INVALID_EN.getOutTextKey(), true, OutTextConfig.LABEL_ACCOMMODATION_SUBTITLE_EN.getOutTextKey(), true ,OutTextConfig.LABEL_ACCOMMODATION_SUBTITLE_LINK_EN.getOutTextKey(), true, null, false, null, false, null, false, null, false),
  HOST_YES(             EventConfig.REDANZ_EVENT, EventPartConfig.HOST_YES             , OutTextConfig.LABEL_HOST_YES_TITLE_EN.getOutTextKey()             , true ,OutTextConfig.LABEL_HOST_YES_TITLE_EXIST_EN.getOutTextKey(), true, null, false, null, false, null, false, null, false, null, false, null, false, null, false),
  HOST_COUNT(           EventConfig.REDANZ_EVENT, EventPartConfig.HOST_COUNT           , OutTextConfig.LABEL_HOST_COUNT_TITLE_EN.getOutTextKey()           , true ,OutTextConfig.LABEL_HOST_COUNT_TITLE_EXIST_EN.getOutTextKey(), true, null, false, null, false, null, false, OutTextConfig.LABEL_HOST_COUNT_HINT_EN.getOutTextKey(), false, null, false, null, false, null, false),
  HOST_DAYS(            EventConfig.REDANZ_EVENT, EventPartConfig.HOST_DAYS            , OutTextConfig.LABEL_HOST_DAYS_TITLE_EN.getOutTextKey()            , true ,OutTextConfig.LABEL_HOST_DAYS_TITLE_EXIST_EN.getOutTextKey(), true, null, false, null, false, null, false, null, false, null, false, null, false, null, false),
  HOST_SPOTS(           EventConfig.REDANZ_EVENT, EventPartConfig.HOST_SPOTS           , OutTextConfig.LABEL_HOST_SPOTS_TITLE_EN.getOutTextKey()           , true ,OutTextConfig.LABEL_HOST_SPOTS_TITLE_EXIST_EN.getOutTextKey(), true, null, false,null, false, null, false  , null, false, null, false, null, false,null, false),
  HOST_COMMENT(         EventConfig.REDANZ_EVENT, EventPartConfig.HOST_COMMENT         , OutTextConfig.LABEL_HOST_COMMENT_TITLE_EN.getOutTextKey()         , true ,OutTextConfig.LABEL_HOST_COMMENT_TITLE_EXIST_EN.getOutTextKey(), true, null, false,null, false, null, false ,OutTextConfig.LABEL_HOST_COMMENT_HINT_EN.getOutTextKey(), true,null, false,null, false,null, false),
  HOSTEE_YES(           EventConfig.REDANZ_EVENT, EventPartConfig.HOSTEE_YES           , OutTextConfig.LABEL_HOSTEE_YES_TITLE_EN.getOutTextKey()           , true ,OutTextConfig.LABEL_HOSTEE_YES_TITLE_EXIST_EN.getOutTextKey(), true, null, false, null, false, null, false, null, false, null, false, null, false, null, false),
  HOSTEE_SHARE(         EventConfig.REDANZ_EVENT, EventPartConfig.HOSTEE_SHARE         , OutTextConfig.LABEL_HOSTEE_SHARE_TITLE_EN.getOutTextKey()         , true ,OutTextConfig.LABEL_HOSTEE_SHARE_TITLE_EXIST_EN.getOutTextKey(), true, null, false, null, false, null, false, null, false, null, false, null, false, null, false),
  HOSTEE_SHARE_PERSON(  EventConfig.REDANZ_EVENT, EventPartConfig.HOSTEE_SHARE_PERSON  , OutTextConfig.LABEL_HOSTEE_SHARE_PERSON_TITLE_EN.getOutTextKey()  , true ,OutTextConfig.LABEL_HOSTEE_SHARE_PERSON_TITLE_EXIST_EN.getOutTextKey(), true, null, false, null, false,null, false, OutTextConfig.LABEL_HOSTEE_SHARE_PERSON_HINT_EN.getOutTextKey(), true,null, false,null, false, null, false),
  HOSTEE_SHARE_BEDS(    EventConfig.REDANZ_EVENT, EventPartConfig.HOSTEE_SHARE_BEDS    , OutTextConfig.LABEL_HOSTEE_SHARE_BEDS_TITLE_EN.getOutTextKey()    , true ,OutTextConfig.LABEL_HOSTEE_SHARE_BEDS_TITLE_EXIST_EN.getOutTextKey(), true, null,  false, null, false, null, false, null, false, null, false, null, false, null, false),
  HOSTEE_DAYS(          EventConfig.REDANZ_EVENT, EventPartConfig.HOSTEE_DAYS          , OutTextConfig.LABEL_HOSTEE_DAYS_TITLE_EN.getOutTextKey()          , true ,OutTextConfig.LABEL_HOSTEE_DAYS_TITLE_EXIST_EN.getOutTextKey(), true, null, false, null, false, null, false, null, false, null, false, null, false, null, false),
  HOSTEE_SPOTS(         EventConfig.REDANZ_EVENT, EventPartConfig.HOSTEE_SPOTS         , OutTextConfig.LABEL_HOSTEE_SPOTS_TITLE_EN.getOutTextKey()         , true ,OutTextConfig.LABEL_HOSTEE_SPOTS_TITLE_EXIST_EN.getOutTextKey(), true, null, false, null, false, null, false, null, false, null, false, null, false, null, false),
  HOSTEE_COMMENT(       EventConfig.REDANZ_EVENT, EventPartConfig.HOSTEE_COMMENT       , OutTextConfig.LABEL_HOSTEE_COMMENT_TITLE_EN.getOutTextKey()       , true ,OutTextConfig.LABEL_HOSTEE_COMMENT_TITLE_EXIST_EN.getOutTextKey(), true, null, false,null, false,null ,false,OutTextConfig.LABEL_HOSTEE_COMMENT_HINT_EN.getOutTextKey(), true,null, false,null, false,null, false),

//  HOST_INFO(            EventConfig.REDANZ_EVENT, EventPartConfig.HOST            , OutTextConfig.LABEL_HOST_TITLE_EN.getOutTextKey()             ,true ,OutTextConfig.LABEL_HOST_TITLE_EXIST_EN.getOutTextKey(), true, null, false, null, false, null, false, null, false, null, false, null, false, null, false),
//  HOSTEE_INFO(          EventConfig.REDANZ_EVENT, EventPartConfig.HOSTEE          , OutTextConfig.LABEL_HOSTEE_TITLE_EN.getOutTextKey()           ,true ,OutTextConfig.LABEL_HOSTEE_TITLE_EXIST_EN.getOutTextKey(), true, null, false, null, false, null, false, null, false, null, false, null, false, null, false),

  // Volunteering
  VOLUNTEER_INFO(          EventConfig.REDANZ_EVENT, EventPartConfig.VOLUNTEER                , OutTextConfig.LABEL_VOLUNTEER_TITLE_EN.getOutTextKey()             ,true , OutTextConfig.LABEL_VOLUNTEER_TITLE_EXIST_EN.getOutTextKey(), true, OutTextConfig.LABEL_VOLUNTEER_INVALID_EN.getOutTextKey(), true, OutTextConfig.LABEL_VOLUNTEER_SUBTITLE_EN.getOutTextKey(), true, OutTextConfig.LABEL_VOLUNTEER_SUBTITLE_LINK_EN.getOutTextKey(), true, null, false, null, false, null, false, null, false),
  VOLUNTEER_YES(           EventConfig.REDANZ_EVENT, EventPartConfig.VOLUNTEER_YES            , OutTextConfig.LABEL_VOLUNTEER_YES_TITLE_EN.getOutTextKey()         ,true , OutTextConfig.LABEL_VOLUNTEER_YES_TITLE_EXIST_EN.getOutTextKey(), true, null, false, null, false, null, false,OutTextConfig.LABEL_VOLUNTEER_YES_HINT_EN.getOutTextKey(), true, null, false, null, false, null, false),
  VOLUNTEER_DURATION(      EventConfig.REDANZ_EVENT, EventPartConfig.VOLUNTEER_DURATION       , OutTextConfig.LABEL_VOLUNTEER_DURATION_TITLE_EN.getOutTextKey()    ,true , OutTextConfig.LABEL_VOLUNTEER_DURATION_TITLE_EXIST_EN.getOutTextKey(), true, null, false, null, false, null, false,OutTextConfig.LABEL_VOLUNTEER_DURATION_HINT_EN.getOutTextKey(), true, null, false,null, false, null, false),
  VOLUNTEER_SLOTS(         EventConfig.REDANZ_EVENT, EventPartConfig.VOLUNTEER_SLOTS          , OutTextConfig.LABEL_VOLUNTEER_SLOTS_TITLE_EN.getOutTextKey()       ,true , OutTextConfig.LABEL_VOLUNTEER_SLOTS_TITLE_EXIST_EN.getOutTextKey(), true, null, false, null, false, null, false,OutTextConfig.LABEL_VOLUNTEER_SLOTS_HINT_EN.getOutTextKey(), true, null, false, null, false, null, false),
  VOLUNTEER_INTRO(         EventConfig.REDANZ_EVENT, EventPartConfig.VOLUNTEER_INTRO          , OutTextConfig.LABEL_VOLUNTEER_INTRO_TITLE_EN.getOutTextKey()       ,true , OutTextConfig.LABEL_VOLUNTEER_INTRO_TITLE_EXIST_EN.getOutTextKey(), true, null, false, null, false, null, false,OutTextConfig.LABEL_VOLUNTEER_INTRO_HINT_EN.getOutTextKey(), true, null, false, null, false, null, false),
  VOLUNTEER_MOBILE(        EventConfig.REDANZ_EVENT, EventPartConfig.VOLUNTEER_MOBILE         , OutTextConfig.LABEL_VOLUNTEER_MOBILE_TITLE_EN.getOutTextKey()      ,true , OutTextConfig.LABEL_VOLUNTEER_MOBILE_TITLE_EXIST_EN.getOutTextKey(), true, null, false, null, false, null, false,OutTextConfig.LABEL_VOLUNTEER_MOBILE_HINT_EN.getOutTextKey(), true, null, false, OutTextConfig.LABEL_VOLUNTEER_MOBILE_HINT2_EN.getOutTextKey(), true, null, false),

  // Solidarity Fund
  SOLIDARITY_FUND_INFO(   EventConfig.REDANZ_EVENT, EventPartConfig.SOLIDARITY_FUND           ,OutTextConfig.LABEL_SOLIDARITY_FUND_TITLE_EN.getOutTextKey()       ,true, OutTextConfig.LABEL_SOLIDARITY_FUND_TITLE_EXIST_EN.getOutTextKey()  ,true, OutTextConfig.LABEL_SOLIDARITY_FUND_INVALID_EN.getOutTextKey(), true, OutTextConfig.LABEL_SOLIDARITY_FUND_SUBTITLE_EN.getOutTextKey(), true,OutTextConfig.LABEL_SOLIDARITY_FUND_SUBTITLE_LINK_EN.getOutTextKey(), true, null, false, null, false, null, false, null, false),
  SCHOLARSHIP_YES(        EventConfig.REDANZ_EVENT, EventPartConfig.SCHOLARSHIP_YES           ,OutTextConfig.LABEL_SCHOLARSHIP_YES_TITLE_EN.getOutTextKey()       ,true, OutTextConfig.LABEL_SCHOLARSHIP_YES_TITLE_EXIST_EN.getOutTextKey()  ,true, null, false, null, false, null, false ,OutTextConfig.LABEL_SCHOLARSHIP_YES_HINT_EN.getOutTextKey()  , true, null, false, null, false, null, false),
  SCHOLARSHIP_DURATION(   EventConfig.REDANZ_EVENT, EventPartConfig.SCHOLARSHIP_INTRO         ,OutTextConfig.LABEL_SCHOLARSHIP_INTRO_TITLE_EN.getOutTextKey()     ,true, OutTextConfig.LABEL_SCHOLARSHIP_INTRO_TITLE_EXIST_EN.getOutTextKey(),true, null, false, null, false, null, false ,OutTextConfig.LABEL_SCHOLARSHIP_INTRO_HINT_EN.getOutTextKey(), true, null, false, OutTextConfig.LABEL_SCHOLARSHIP_INTRO_HINT2_EN.getOutTextKey(), true, null, false),
  DONATION_YES(           EventConfig.REDANZ_EVENT, EventPartConfig.DONATION_YES              ,OutTextConfig.LABEL_DONATION_YES_TITLE_EN.getOutTextKey()          ,true, OutTextConfig.LABEL_DONATION_YES_TITLE_EXIST_EN.getOutTextKey()     ,true, null, false, null, false, null, false ,OutTextConfig.LABEL_DONATION_YES_HINT_EN.getOutTextKey()     , true, null, false, null, false, null, false),
  DONATION_DURATION(      EventConfig.REDANZ_EVENT, EventPartConfig.DONATION_AMOUNT           ,OutTextConfig.LABEL_DONATION_AMOUNT_TITLE_EN.getOutTextKey()       ,true, OutTextConfig.LABEL_DONATION_AMOUNT_TITLE_EXIST_EN.getOutTextKey()  ,true, null,false, null, false, null, false, null, false, null, true, null, false, null, false),

  // Terms
  TERMS_INFO(          EventConfig.REDANZ_EVENT, EventPartConfig.TERMS    , OutTextConfig.LABEL_TERMS_TITLE_EN.getOutTextKey()              , true, OutTextConfig.LABEL_TERMS_TITLE_EXIST_EN.getOutTextKey(), true, OutTextConfig.LABEL_TERMS_INVALID_EN.getOutTextKey(), true,null, false,null, false,  OutTextConfig.LABEL_TERMS_HINT_EN.getOutTextKey(), true,  OutTextConfig.LABEL_TERMS_HINT_LINK_EN.getOutTextKey(), true,  OutTextConfig.LABEL_TERMS_HINT2_EN.getOutTextKey(), true,  OutTextConfig.LABEL_TERMS_HINT2_LINK_EN.getOutTextKey(), true),
  ;
  private final EventConfig eventConfig;
  public final EventPartConfig eventPartConfig;
  private final String title;
  private final Boolean titleRequired;
  private final String titleExist;
  private final Boolean titleExistRequired;
  private final String invalid;
  private final Boolean invalidRequired;
  private final String subtitle;
  private final Boolean subtitleRequired;
  private final String subtitleLink;
  private final Boolean subtitleLinkRequired;
  private final String hint;
  private final Boolean hintRequired;
  private final String hintLink;
  private final Boolean hintLinkRequired;
  private final String hint2;
  private final Boolean hint2Required;
  private final String hint2Link;
  private final Boolean hint2LinkRequired;

  public static void setup(EventService eventService, EventPartService eventPartService) {
    for (EventPartInfoConfig eventPartInfoConfig : EventPartInfoConfig.values()) {
      Event event = eventService.findByName(eventPartInfoConfig.eventConfig.getName());
      EventPart eventPart = eventPartService.findByKey(eventPartInfoConfig.eventPartConfig.getEventPartKey());
      eventService.save(
        new EventPartInfo(
          event,
          eventPart,
          eventPartInfoConfig.title,
          eventPartInfoConfig.titleRequired,
          eventPartInfoConfig.titleExist,
          eventPartInfoConfig.titleExistRequired,
          eventPartInfoConfig.invalid,
          eventPartInfoConfig.invalidRequired,
          eventPartInfoConfig.subtitle,
          eventPartInfoConfig.subtitleRequired,
          eventPartInfoConfig.subtitleLink,
          eventPartInfoConfig.subtitleLinkRequired,
          eventPartInfoConfig.hint,
          eventPartInfoConfig.hintRequired,
          eventPartInfoConfig.hintLink,
          eventPartInfoConfig.hintLinkRequired,
          eventPartInfoConfig.hint2,
          eventPartInfoConfig.hint2Required,
          eventPartInfoConfig.hint2Link,
          eventPartInfoConfig.hint2LinkRequired
        )
      );
    }
  }
}
