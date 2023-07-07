package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.profile.entities.Country;
import ch.redanz.redanzCore.model.profile.service.CountryService;
import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.workshop.entities.OutText;
import ch.redanz.redanzCore.model.workshop.entities.OutTextId;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@AllArgsConstructor
public enum CountryConfig {

  COUNTRY_AF("Afghanistan","af", OutTextConfig.LABEL_COUNTRY_AF_EN),
  COUNTRY_AL("Albania","al", OutTextConfig.LABEL_COUNTRY_AL_EN),
  COUNTRY_DZ("Algeria","dz", OutTextConfig.LABEL_COUNTRY_DZ_EN),
  COUNTRY_AD("Andorra","ad", OutTextConfig.LABEL_COUNTRY_AD_EN),
  COUNTRY_AO("Angola","ao", OutTextConfig.LABEL_COUNTRY_AO_EN),
  COUNTRY_AG("Antigua and Barbuda","ag", OutTextConfig.LABEL_COUNTRY_AG_EN),
  COUNTRY_AZ("Azerbaijan","az", OutTextConfig.LABEL_COUNTRY_AZ_EN),
  COUNTRY_AR("Argentina","ar", OutTextConfig.LABEL_COUNTRY_AR_EN),
  COUNTRY_AU("Australia","au", OutTextConfig.LABEL_COUNTRY_AU_EN),
  COUNTRY_AT("Austria","at", OutTextConfig.LABEL_COUNTRY_AT_EN),
  COUNTRY_BS("Bahamas","bs", OutTextConfig.LABEL_COUNTRY_BS_EN),
  COUNTRY_BH("Bahrain","bh", OutTextConfig.LABEL_COUNTRY_BH_EN),
  COUNTRY_BD("Bangladesh","bd", OutTextConfig.LABEL_COUNTRY_BD_EN),
  COUNTRY_AM("Armenia","am", OutTextConfig.LABEL_COUNTRY_AM_EN),
  COUNTRY_BB("Barbados","bb", OutTextConfig.LABEL_COUNTRY_BB_EN),
  COUNTRY_BE("Belgium","be", OutTextConfig.LABEL_COUNTRY_BE_EN),
  COUNTRY_BT("Bhutan","bt", OutTextConfig.LABEL_COUNTRY_BT_EN),
  COUNTRY_BO("Bolivia","bo", OutTextConfig.LABEL_COUNTRY_BO_EN),
  COUNTRY_BA("Bosnia and Herzegovina","ba", OutTextConfig.LABEL_COUNTRY_BA_EN),
  COUNTRY_BW("Botswana","bw", OutTextConfig.LABEL_COUNTRY_BW_EN),
  COUNTRY_BR("Brazil","br", OutTextConfig.LABEL_COUNTRY_BR_EN),
  COUNTRY_BZ("Belize","bz", OutTextConfig.LABEL_COUNTRY_BZ_EN),
  COUNTRY_SB("Solomon Islands","sb", OutTextConfig.LABEL_COUNTRY_SB_EN),
  COUNTRY_BN("Brunei Darussalam","bn", OutTextConfig.LABEL_COUNTRY_BN_EN),
  COUNTRY_BG("Bulgaria","bg", OutTextConfig.LABEL_COUNTRY_BG_EN),
  COUNTRY_MM("Myanmar","mm", OutTextConfig.LABEL_COUNTRY_MM_EN),
  COUNTRY_BI("Burundi","bi", OutTextConfig.LABEL_COUNTRY_BI_EN),
  COUNTRY_BY("Belarus","by", OutTextConfig.LABEL_COUNTRY_BY_EN),
  COUNTRY_KH("Cambodia","kh", OutTextConfig.LABEL_COUNTRY_KH_EN),
  COUNTRY_CM("Cameroon","cm", OutTextConfig.LABEL_COUNTRY_CM_EN),
  COUNTRY_CA("Canada","ca", OutTextConfig.LABEL_COUNTRY_CA_EN),
  COUNTRY_CV("Cabo Verde","cv", OutTextConfig.LABEL_COUNTRY_CV_EN),
  COUNTRY_CF("Central African Republic","cf", OutTextConfig.LABEL_COUNTRY_CF_EN),
  COUNTRY_LK("Sri Lanka","lk", OutTextConfig.LABEL_COUNTRY_LK_EN),
  COUNTRY_TD("Chad","td", OutTextConfig.LABEL_COUNTRY_TD_EN),
  COUNTRY_CL("Chile","cl", OutTextConfig.LABEL_COUNTRY_CL_EN),
  COUNTRY_CN("China","cn", OutTextConfig.LABEL_COUNTRY_CN_EN),
  COUNTRY_CO("Colombia","co", OutTextConfig.LABEL_COUNTRY_CO_EN),
  COUNTRY_KM("Comoros","km", OutTextConfig.LABEL_COUNTRY_KM_EN),
  COUNTRY_CG("Congo","cg", OutTextConfig.LABEL_COUNTRY_CG_EN),
  COUNTRY_CD("Congo","cd", OutTextConfig.LABEL_COUNTRY_CD_EN),
  COUNTRY_CR("Costa Rica","cr", OutTextConfig.LABEL_COUNTRY_CR_EN),
  COUNTRY_HR("Croatia","hr", OutTextConfig.LABEL_COUNTRY_HR_EN),
  COUNTRY_CU("Cuba","cu", OutTextConfig.LABEL_COUNTRY_CU_EN),
  COUNTRY_CY("Cyprus","cy", OutTextConfig.LABEL_COUNTRY_CY_EN),
  COUNTRY_CZ("Czechia","cz", OutTextConfig.LABEL_COUNTRY_CZ_EN),
  COUNTRY_BJ("Benin","bj", OutTextConfig.LABEL_COUNTRY_BJ_EN),
  COUNTRY_DK("Denmark","dk", OutTextConfig.LABEL_COUNTRY_DK_EN),
  COUNTRY_DM("Dominica","dm", OutTextConfig.LABEL_COUNTRY_DM_EN),
  COUNTRY_DO("Dominican Republic","do", OutTextConfig.LABEL_COUNTRY_DO_EN),
  COUNTRY_EC("Ecuador","ec", OutTextConfig.LABEL_COUNTRY_EC_EN),
  COUNTRY_SV("El Salvador","sv", OutTextConfig.LABEL_COUNTRY_SV_EN),
  COUNTRY_GQ("Equatorial Guinea","gq", OutTextConfig.LABEL_COUNTRY_GQ_EN),
  COUNTRY_ET("Ethiopia","et", OutTextConfig.LABEL_COUNTRY_ET_EN),
  COUNTRY_ER("Eritrea","er", OutTextConfig.LABEL_COUNTRY_ER_EN),
  COUNTRY_EE("Estonia","ee", OutTextConfig.LABEL_COUNTRY_EE_EN),
  COUNTRY_FJ("Fiji","fj", OutTextConfig.LABEL_COUNTRY_FJ_EN),
  COUNTRY_FI("Finland","fi", OutTextConfig.LABEL_COUNTRY_FI_EN),
  COUNTRY_FR("France","fr", OutTextConfig.LABEL_COUNTRY_FR_EN),
  COUNTRY_DJ("Djibouti","dj", OutTextConfig.LABEL_COUNTRY_DJ_EN),
  COUNTRY_GA("Gabon","ga", OutTextConfig.LABEL_COUNTRY_GA_EN),
  COUNTRY_GE("Georgia","ge", OutTextConfig.LABEL_COUNTRY_GE_EN),
  COUNTRY_GM("Gambia","gm", OutTextConfig.LABEL_COUNTRY_GM_EN),
  COUNTRY_DE("Germany","de", OutTextConfig.LABEL_COUNTRY_DE_EN),
  COUNTRY_GH("Ghana","gh", OutTextConfig.LABEL_COUNTRY_GH_EN),
  COUNTRY_KI("Kiribati","ki", OutTextConfig.LABEL_COUNTRY_KI_EN),
  COUNTRY_GR("Greece","gr", OutTextConfig.LABEL_COUNTRY_GR_EN),
  COUNTRY_GD("Grenada","gd", OutTextConfig.LABEL_COUNTRY_GD_EN),
  COUNTRY_GT("Guatemala","gt", OutTextConfig.LABEL_COUNTRY_GT_EN),
  COUNTRY_GN("Guinea","gn", OutTextConfig.LABEL_COUNTRY_GN_EN),
  COUNTRY_GY("Guyana","gy", OutTextConfig.LABEL_COUNTRY_GY_EN),
  COUNTRY_HT("Haiti","ht", OutTextConfig.LABEL_COUNTRY_HT_EN),
  COUNTRY_HN("Honduras","hn", OutTextConfig.LABEL_COUNTRY_HN_EN),
  COUNTRY_HU("Hungary","hu", OutTextConfig.LABEL_COUNTRY_HU_EN),
  COUNTRY_IS("Iceland","is", OutTextConfig.LABEL_COUNTRY_IS_EN),
  COUNTRY_IN("India","in", OutTextConfig.LABEL_COUNTRY_IN_EN),
  COUNTRY_ID("Indonesia","id", OutTextConfig.LABEL_COUNTRY_ID_EN),
  COUNTRY_IR("Iran","ir", OutTextConfig.LABEL_COUNTRY_IR_EN),
  COUNTRY_IQ("Iraq","iq", OutTextConfig.LABEL_COUNTRY_IQ_EN),
  COUNTRY_IE("Ireland","ie", OutTextConfig.LABEL_COUNTRY_IE_EN),
  COUNTRY_IL("Israel","il", OutTextConfig.LABEL_COUNTRY_IL_EN),
  COUNTRY_IT("Italy","it", OutTextConfig.LABEL_COUNTRY_IT_EN),
  COUNTRY_CI("Côte d''Ivoire","ci", OutTextConfig.LABEL_COUNTRY_CI_EN),
  COUNTRY_JM("Jamaica","jm", OutTextConfig.LABEL_COUNTRY_JM_EN),
  COUNTRY_JP("Japan","jp", OutTextConfig.LABEL_COUNTRY_JP_EN),
  COUNTRY_KZ("Kazakhstan","kz", OutTextConfig.LABEL_COUNTRY_KZ_EN),
  COUNTRY_JO("Jordan","jo", OutTextConfig.LABEL_COUNTRY_JO_EN),
  COUNTRY_KE("Kenya","ke", OutTextConfig.LABEL_COUNTRY_KE_EN),
  COUNTRY_KP("North Korea","kp", OutTextConfig.LABEL_COUNTRY_KP_EN),
  COUNTRY_KR("South Korea","kr", OutTextConfig.LABEL_COUNTRY_KR_EN),
  COUNTRY_KW("Kuwait","kw", OutTextConfig.LABEL_COUNTRY_KW_EN),
  COUNTRY_KG("Kyrgyzstan","kg", OutTextConfig.LABEL_COUNTRY_KG_EN),
  COUNTRY_LA("Lao","la", OutTextConfig.LABEL_COUNTRY_LA_EN),
  COUNTRY_LB("Lebanon","lb", OutTextConfig.LABEL_COUNTRY_LB_EN),
  COUNTRY_LS("Lesotho","ls", OutTextConfig.LABEL_COUNTRY_LS_EN),
  COUNTRY_LV("Latvia","lv", OutTextConfig.LABEL_COUNTRY_LV_EN),
  COUNTRY_LR("Liberia","lr", OutTextConfig.LABEL_COUNTRY_LR_EN),
  COUNTRY_LY("Libya","ly", OutTextConfig.LABEL_COUNTRY_LY_EN),
  COUNTRY_LI("Liechtenstein","li", OutTextConfig.LABEL_COUNTRY_LI_EN),
  COUNTRY_LT("Lithuania","lt", OutTextConfig.LABEL_COUNTRY_LT_EN),
  COUNTRY_LU("Luxembourg","lu", OutTextConfig.LABEL_COUNTRY_LU_EN),
  COUNTRY_MG("Madagascar","mg", OutTextConfig.LABEL_COUNTRY_MG_EN),
  COUNTRY_MW("Malawi","mw", OutTextConfig.LABEL_COUNTRY_MW_EN),
  COUNTRY_MY("Malaysia","my", OutTextConfig.LABEL_COUNTRY_MY_EN),
  COUNTRY_MV("Maldives","mv", OutTextConfig.LABEL_COUNTRY_MV_EN),
  COUNTRY_ML("Mali","ml", OutTextConfig.LABEL_COUNTRY_ML_EN),
  COUNTRY_MT("Malta","mt", OutTextConfig.LABEL_COUNTRY_MT_EN),
  COUNTRY_MR("Mauritania","mr", OutTextConfig.LABEL_COUNTRY_MR_EN),
  COUNTRY_MU("Mauritius","mu", OutTextConfig.LABEL_COUNTRY_MU_EN),
  COUNTRY_MX("Mexico","mx", OutTextConfig.LABEL_COUNTRY_MX_EN),
  COUNTRY_MC("Monaco","mc", OutTextConfig.LABEL_COUNTRY_MC_EN),
  COUNTRY_MN("Mongolia","mn", OutTextConfig.LABEL_COUNTRY_MN_EN),
  COUNTRY_MD("Moldova","md", OutTextConfig.LABEL_COUNTRY_MD_EN),
  COUNTRY_ME("Montenegro","me", OutTextConfig.LABEL_COUNTRY_ME_EN),
  COUNTRY_MA("Morocco","ma", OutTextConfig.LABEL_COUNTRY_MA_EN),
  COUNTRY_MZ("Mozambique","mz", OutTextConfig.LABEL_COUNTRY_MZ_EN),
  COUNTRY_OM("Oman","om", OutTextConfig.LABEL_COUNTRY_OM_EN),
  COUNTRY_NA("Namibia","na", OutTextConfig.LABEL_COUNTRY_NA_EN),
  COUNTRY_NR("Nauru","nr", OutTextConfig.LABEL_COUNTRY_NR_EN),
  COUNTRY_NP("Nepal","np", OutTextConfig.LABEL_COUNTRY_NP_EN),
  COUNTRY_NL("Netherlands","nl", OutTextConfig.LABEL_COUNTRY_NL_EN),
  COUNTRY_VU("Vanuatu","vu", OutTextConfig.LABEL_COUNTRY_VU_EN),
  COUNTRY_NZ("New Zealand","nz", OutTextConfig.LABEL_COUNTRY_NZ_EN),
  COUNTRY_NI("Nicaragua","ni", OutTextConfig.LABEL_COUNTRY_NI_EN),
  COUNTRY_NE("Niger","ne", OutTextConfig.LABEL_COUNTRY_NE_EN),
  COUNTRY_NG("Nigeria","ng", OutTextConfig.LABEL_COUNTRY_NG_EN),
  COUNTRY_NO("Norway","no", OutTextConfig.LABEL_COUNTRY_NO_EN),
  COUNTRY_FM("Micronesia","fm", OutTextConfig.LABEL_COUNTRY_FM_EN),
  COUNTRY_MH("Marshall Islands","mh", OutTextConfig.LABEL_COUNTRY_MH_EN),
  COUNTRY_PW("Palau","pw", OutTextConfig.LABEL_COUNTRY_PW_EN),
  COUNTRY_PK("Pakistan","pk", OutTextConfig.LABEL_COUNTRY_PK_EN),
  COUNTRY_PA("Panama","pa", OutTextConfig.LABEL_COUNTRY_PA_EN),
  COUNTRY_PG("Papua New Guinea","pg", OutTextConfig.LABEL_COUNTRY_PG_EN),
  COUNTRY_PY("Paraguay","py", OutTextConfig.LABEL_COUNTRY_PY_EN),
  COUNTRY_PE("Peru","pe", OutTextConfig.LABEL_COUNTRY_PE_EN),
  COUNTRY_PH("Philippines","ph", OutTextConfig.LABEL_COUNTRY_PH_EN),
  COUNTRY_PL("Poland","pl", OutTextConfig.LABEL_COUNTRY_PL_EN),
  COUNTRY_PT("Portugal","pt", OutTextConfig.LABEL_COUNTRY_PT_EN),
  COUNTRY_GW("Guinea-Bissau","gw", OutTextConfig.LABEL_COUNTRY_GW_EN),
  COUNTRY_TL("Timor-Leste","tl", OutTextConfig.LABEL_COUNTRY_TL_EN),
  COUNTRY_QA("Qatar","qa", OutTextConfig.LABEL_COUNTRY_QA_EN),
  COUNTRY_RO("Romania","ro", OutTextConfig.LABEL_COUNTRY_RO_EN),
  COUNTRY_RU("Russian Federation","ru", OutTextConfig.LABEL_COUNTRY_RU_EN),
  COUNTRY_RW("Rwanda","rw", OutTextConfig.LABEL_COUNTRY_RW_EN),
  COUNTRY_KN("Saint Kitts and Nevis","kn", OutTextConfig.LABEL_COUNTRY_KN_EN),
  COUNTRY_LC("Saint Lucia","lc", OutTextConfig.LABEL_COUNTRY_LC_EN),
  COUNTRY_VC("Saint Vincent and the Grenadines","vc", OutTextConfig.LABEL_COUNTRY_VC_EN),
  COUNTRY_SM("San Marino","sm", OutTextConfig.LABEL_COUNTRY_SM_EN),
  COUNTRY_ST("Sao Tome and Principe","st", OutTextConfig.LABEL_COUNTRY_ST_EN),
  COUNTRY_SA("Saudi Arabia","sa", OutTextConfig.LABEL_COUNTRY_SA_EN),
  COUNTRY_SN("Senegal","sn", OutTextConfig.LABEL_COUNTRY_SN_EN),
  COUNTRY_RS("Serbia","rs", OutTextConfig.LABEL_COUNTRY_RS_EN),
  COUNTRY_SC("Seychelles","sc", OutTextConfig.LABEL_COUNTRY_SC_EN),
  COUNTRY_SL("Sierra Leone","sl", OutTextConfig.LABEL_COUNTRY_SL_EN),
  COUNTRY_SG("Singapore","sg", OutTextConfig.LABEL_COUNTRY_SG_EN),
  COUNTRY_SK("Slovakia","sk", OutTextConfig.LABEL_COUNTRY_SK_EN),
  COUNTRY_VN("Viet Nam","vn", OutTextConfig.LABEL_COUNTRY_VN_EN),
  COUNTRY_SI("Slovenia","si", OutTextConfig.LABEL_COUNTRY_SI_EN),
  COUNTRY_SO("Somalia","so", OutTextConfig.LABEL_COUNTRY_SO_EN),
  COUNTRY_ZA("South Africa","za", OutTextConfig.LABEL_COUNTRY_ZA_EN),
  COUNTRY_ZW("Zimbabwe","zw", OutTextConfig.LABEL_COUNTRY_ZW_EN),
  COUNTRY_ES("Spain","es", OutTextConfig.LABEL_COUNTRY_ES_EN),
  COUNTRY_SS("South Sudan","ss", OutTextConfig.LABEL_COUNTRY_SS_EN),
  COUNTRY_SD("Sudan","sd", OutTextConfig.LABEL_COUNTRY_SD_EN),
  COUNTRY_SR("Suriname","sr", OutTextConfig.LABEL_COUNTRY_SR_EN),
  COUNTRY_SZ("Eswatini","sz", OutTextConfig.LABEL_COUNTRY_SZ_EN),
  COUNTRY_SE("Sweden","se", OutTextConfig.LABEL_COUNTRY_SE_EN),
  COUNTRY_CH("Switzerland","ch", OutTextConfig.LABEL_COUNTRY_CH_EN),
  COUNTRY_SY("Syrian Arab Republic","sy", OutTextConfig.LABEL_COUNTRY_SY_EN),
  COUNTRY_TJ("Tajikistan","tj", OutTextConfig.LABEL_COUNTRY_TJ_EN),
  COUNTRY_TH("Thailand","th", OutTextConfig.LABEL_COUNTRY_TH_EN),
  COUNTRY_TG("Togo","tg", OutTextConfig.LABEL_COUNTRY_TG_EN),
  COUNTRY_TO("Tonga","to", OutTextConfig.LABEL_COUNTRY_TO_EN),
  COUNTRY_TT("Trinidad and Tobago","tt", OutTextConfig.LABEL_COUNTRY_TT_EN),
  COUNTRY_AE("United Arab Emirates","ae", OutTextConfig.LABEL_COUNTRY_AE_EN),
  COUNTRY_TN("Tunisia","tn", OutTextConfig.LABEL_COUNTRY_TN_EN),
  COUNTRY_TR("Turkey","tr", OutTextConfig.LABEL_COUNTRY_TR_EN),
  COUNTRY_TM("Turkmenistan","tm", OutTextConfig.LABEL_COUNTRY_TM_EN),
  COUNTRY_TV("Tuvalu","tv", OutTextConfig.LABEL_COUNTRY_TV_EN),
  COUNTRY_UG("Uganda","ug", OutTextConfig.LABEL_COUNTRY_UG_EN),
  COUNTRY_UA("Ukraine","ua", OutTextConfig.LABEL_COUNTRY_UA_EN),
  COUNTRY_MK("North Macedonia","mk", OutTextConfig.LABEL_COUNTRY_MK_EN),
  COUNTRY_EG("Egypt","eg", OutTextConfig.LABEL_COUNTRY_EG_EN),
  COUNTRY_GB("United Kingdom","gb", OutTextConfig.LABEL_COUNTRY_GB_EN),
  COUNTRY_TZ("Tanzania, United Republic of","tz", OutTextConfig.LABEL_COUNTRY_TZ_EN),
  COUNTRY_US("United States of America","us", OutTextConfig.LABEL_COUNTRY_US_EN),
  COUNTRY_BF("Burkina Faso","bf", OutTextConfig.LABEL_COUNTRY_BF_EN),
  COUNTRY_UY("Uruguay","uy", OutTextConfig.LABEL_COUNTRY_UY_EN),
  COUNTRY_UZ("Uzbekistan","uz", OutTextConfig.LABEL_COUNTRY_UZ_EN),
  COUNTRY_VE("Venezuela","ve", OutTextConfig.LABEL_COUNTRY_VE_EN),
  COUNTRY_WS("Samoa","ws", OutTextConfig.LABEL_COUNTRY_WS_EN),
  COUNTRY_YE("Yemen","ye", OutTextConfig.LABEL_COUNTRY_YE_EN),
  COUNTRY_ZM("Zambia","zm", OutTextConfig.LABEL_COUNTRY_ZM_EN)
  ;

  private final String name;
  private final String sortName;
  private final OutTextConfig outTextConfig;

  public static void setup(CountryService countryService, LanguageService languageService) {
    for (CountryConfig countryConfig : CountryConfig.values()) {
      if (countryService.countryExists(countryConfig.sortName)) {
        Country country = countryService.findCountry(countryConfig.sortName);
        country.setName(countryConfig.name);
        country.setOutTextKey(countryConfig.outTextConfig.getOutTextKey());
        countryService.save(country);
      } else {
        countryService.save(
          new Country(
            countryConfig.sortName,
            countryConfig.name,
            countryConfig.outTextConfig.getOutTextKey()
          )
        );
      }
    }
  }
}
