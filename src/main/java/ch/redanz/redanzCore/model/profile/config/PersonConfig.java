package ch.redanz.redanzCore.model.profile.config;

import ch.redanz.redanzCore.model.profile.Person;
import ch.redanz.redanzCore.model.profile.repository.UserRepo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@AllArgsConstructor
public enum PersonConfig {
    FRANKY (    UserConfig.FRANKY_USER  ,"Franky"   , "Manning"     , "Bahnhofstrasse 1", "8000", "Zürich"),
    CLAUDIA(    UserConfig.CLAUDIA_USER ,"Claudia"  , "Fonte"       , "Bahnhofstrasse 1", "8000", "Zürich"),
    NORMA  (    UserConfig.NORMA_USER   ,"Norma"    , "Miller"      , "Bahnhofstrasse 1", "8000", "Zürich"),
    EDDIE  (    UserConfig.EDDIE_USER   ,"Eddie"    , "Davis"       , "Bahnhofstrasse 1", "8000", "Zürich"),
    WILLIAM(    UserConfig.WILLIAM_USER ,"William"  , "Downes"      , "Bahnhofstrasse 1", "8000", "Zürich"),
    ELNORA (    UserConfig.ELNORA_USER  ,"Elnora"   , "Dyson"       , "Bahnhofstrasse 1", "8000", "Zürich"),
    ARLYNE (    UserConfig.ARLYNE_USER  ,"Arlyne"   , "Evans"       , "Bahnhofstrasse 1", "8000", "Zürich"),
    BILLY  (    UserConfig.BILLY_USER   ,"Billy"    , "Ricker"      , "Bahnhofstrasse 1", "8000", "Zürich"),
    NAOMI  (    UserConfig.NAOMI_USER   ,"Naomi"    , "Waller"      , "Bahnhofstrasse 1", "8000", "Zürich"),
    ESTHER (    UserConfig.ESTHER_USER  ,"Esther"   , "Washington"  , "Bahnhofstrasse 1", "8000", "Zürich"),
    ANN (       UserConfig.ANN_USER     ,"Ann"      , "Johnson"     , "Bahnhofstrasse 1", "8000", "Zürich"),
    MILDRED (   UserConfig.MILDRED_USER ,"Mildred"  , "Pollard"     , "Bahnhofstrasse 1", "8000", "Zürich"),
    RUTHIE (    UserConfig.RUTHIE_USER  ,"Ruthie"   , "Reingold"    , "Bahnhofstrasse 1", "8000", "Zürich"),
    WILLA (     UserConfig.WILLA_USER   ,"Willa"    , "Ricker"      , "Bahnhofstrasse 1", "8000", "Zürich"),
    HARRY (     UserConfig.HARRY_USER   ,"Harry"    , "Rosenberg"   , "Bahnhofstrasse 1", "8000", "Zürich"),
    OLIVER (    UserConfig.OLIVER_USER  ,"Oliver"   , "Washington"  , "Bahnhofstrasse 1", "8000", "Zürich"),

    ORG_SONNY(UserConfig.ORG_SONNY_USER, "Sonny", "Jenkins", "Bahnhofstrasse 1", "8000", "Zürich"),
    ORG_ANN(UserConfig.ORG_ANN_USER, "Ann", "Johnson", "Bahnhofstrasse 1", "8000", "Zürich");

    private final UserConfig userConfig;
    private final String firstName;
    private final String lastName;
    private final String street;
    private final String postalCode;
    private final String city;

    public static List<Person> setup(UserRepo userRepo) {
        List<Person> transitions = new ArrayList<>();

        for (PersonConfig personConfig : PersonConfig.values()) {
            transitions.add(
                    new Person(
                            userRepo.findByEmail(personConfig.userConfig.getEmail()),
                            personConfig.firstName,
                            personConfig.lastName,
                            personConfig.street,
                            personConfig.postalCode,
                            personConfig.city
                    )
            );
        }
        return transitions;
    }
}

