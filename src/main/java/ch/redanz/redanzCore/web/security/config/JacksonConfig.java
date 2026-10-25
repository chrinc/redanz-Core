package ch.redanz.redanzCore.web.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.hibernate7.Hibernate7Module;

@Configuration
public class JacksonConfig {

  // Jackson 3 with the defaults of Jackson 2 (same JSON as before the upgrade, e.g. dates as timestamps)
  @Bean
  public JsonMapper jsonMapper() {
    return JsonMapper.builderWithJackson2Defaults()
      .addModule(new Hibernate7Module())
      .build();
  }
}
