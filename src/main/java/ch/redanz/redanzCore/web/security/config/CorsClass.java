package ch.redanz.redanzCore.web.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//@EnableWebMvc
@Slf4j
public class CorsClass implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    log.info("register cross origins");
    registry.addMapping("/**")
      .allowedOrigins("https://redanz.ch")
      .allowedOrigins("http://localhost:4200");
  }
}
