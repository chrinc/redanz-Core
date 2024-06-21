package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.EventPart;
import ch.redanz.redanzCore.model.workshop.service.EventPartService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum EventPartConfig {
    EVENT               ("event"            , OutTextConfig.LABEL_EVENT_INFO_EN.getOutTextKey()),
    BUNDLE              ("bundle"           , OutTextConfig.LABEL_BUNDLE_INFO_EN.getOutTextKey()),
    TRACK               ("track"            , OutTextConfig.LABEL_TRACK_INFO_EN.getOutTextKey()),
    DANCE_ROLE          ("danceRole"        , OutTextConfig.LABEL_DANCE_ROLE_EN.getOutTextKey()),
    PARTNER_REGISTRATION("partnerRegistration" , OutTextConfig.LABEL_PARTNER_REGISTRATION_EN.getOutTextKey()),
    PARTNER_EMAIL       ("partnerEmail"     , OutTextConfig.LABEL_PARTNER_EMAIL_EN.getOutTextKey()),
    SOLIDARITY_FUND     ("solidarityFund"   , OutTextConfig.LABEL_SOLIDARITYFUND_INFO_EN.getOutTextKey()),
    DISCOUNT            ("discount"         , OutTextConfig.LABEL_DISCOUNT_INFO_EN.getOutTextKey()),
    SPECIAL             ("special"          , OutTextConfig.LABEL_SPECIAL_INFO_EN.getOutTextKey()),
    FOOD                ("food"             , OutTextConfig.LABEL_FOOD_INFO_EN.getOutTextKey()),
    ACCOMMODATION       ("accommodation"    , OutTextConfig.LABEL_ACCOMMODATION_INFO_EN.getOutTextKey()),
    VOLUNTEER           ("volunteer"        , OutTextConfig.LABEL_VOLUNTEER_INFO_EN.getOutTextKey()),
    SCHOLARSHIP         ("scholarship"      , OutTextConfig.LABEL_SCHOLARSHIP_INFO_EN.getOutTextKey()),
    DONATION            ("donation"         , OutTextConfig.LABEL_DONATION_INFO_EN.getOutTextKey()),
    TERMS               ("terms"            , OutTextConfig.LABEL_TERMS_INFO_EN.getOutTextKey()),
    PRIVATE             ("private"          , OutTextConfig.LABEL_PRIVATE_INFO_EN.getOutTextKey()),
    HOST                ("host"             , OutTextConfig.LABEL_HOST_INFO_EN.getOutTextKey()),
    HOST_YES            ("hostYes"          , OutTextConfig.LABEL_HOSTYES_INFO_EN.getOutTextKey()),
    HOST_COUNT          ("hostCount"        , OutTextConfig.LABEL_HOSTCOUNT_INFO_EN.getOutTextKey()),
    HOST_DAYS           ("hostDays"         , OutTextConfig.LABEL_HOSTDAYS_INFO_EN.getOutTextKey()),
    HOST_SPOTS          ("hostSpots"        , OutTextConfig.LABEL_HOSTSPOTS_INFO_EN.getOutTextKey()),
    HOST_COMMENT        ("hostComment"      , OutTextConfig.LABEL_HOSTCOMMENT_INFO_EN.getOutTextKey()),
    HOSTEE              ("hostee"           , OutTextConfig.LABEL_HOSTEE_INFO_EN.getOutTextKey()),
    HOSTEE_YES          ("hosteeYes"        , OutTextConfig.LABEL_HOSTEEYES_INFO_EN.getOutTextKey()),
    HOSTEE_SHARE        ("hosteeShare"      , OutTextConfig.LABEL_HOSTEESHARE_INFO_EN.getOutTextKey()),
    HOSTEE_SHARE_PERSON ("hosteeSharePerson", OutTextConfig.LABEL_HOSTEESHAREPERSON_INFO_EN.getOutTextKey()),
    HOSTEE_SHARE_BEDS   ("hosteeShareBeds"  , OutTextConfig.LABEL_HOSTEESHAREBEDS_INFO_EN.getOutTextKey()),
    HOSTEE_DAYS         ("hosteeDays"       , OutTextConfig.LABEL_HOSTEEDAYS_INFO_EN.getOutTextKey()),
    HOSTEE_SPOTS        ("hosteeSpots"      , OutTextConfig.LABEL_HOSTEESPOTS_INFO_EN.getOutTextKey()),
    HOSTEE_COMMENT      ("hosteeComment"   , OutTextConfig.LABEL_HOSTEECOMMENTS_INFO_EN.getOutTextKey()),
    VOLUNTEER_YES       ("volunteerYes"     , OutTextConfig.LABEL_VOLUNTEERYES_INFO_EN.getOutTextKey()),
    VOLUNTEER_DURATION  ("volunteerDuration", OutTextConfig.LABEL_VOLUNTEERDURATION_INFO_EN.getOutTextKey()),
    VOLUNTEER_SLOTS     ("volunteerSlots"   , OutTextConfig.LABEL_VOLUNTEERSLOTS_INFO_EN.getOutTextKey()),
    VOLUNTEER_INTRO     ("volunteerIntro"   , OutTextConfig.LABEL_VOLUNTEERINTRO_INFO_EN.getOutTextKey()),
    VOLUNTEER_MOBILE    ("volunteerMobile"  , OutTextConfig.LABEL_VOLUNTEERMOBILE_INFO_EN.getOutTextKey()),
    SCHOLARSHIP_YES     ("scholarshipYes"   , OutTextConfig.LABEL_SCHOLARSHIPYES_INFO_EN.getOutTextKey()),
    SCHOLARSHIP_INTRO   ("scholarshipIntro" , OutTextConfig.LABEL_SCHOLARSHIPINTRO_INFO_EN.getOutTextKey()),
    DONATION_YES        ("donationYes"      , OutTextConfig.LABEL_DONATIONYES_INFO_EN.getOutTextKey()),
    DONATION_AMOUNT     ("donationAmount"   , OutTextConfig.LABEL_DONATIONAMOUNT_INFO_EN.getOutTextKey());

  private final String eventPartKey;
  private final String name;

    public static void setup(EventPartService eventPartService) {
      for (EventPartConfig eventPartConfig : EventPartConfig.values()) {
        eventPartService.save(
          new EventPart(
            eventPartConfig.getEventPartKey(),
            eventPartConfig.getName()
          )
        );
      }
    }
  }
