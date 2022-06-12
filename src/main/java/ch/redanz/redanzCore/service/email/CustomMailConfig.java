package ch.redanz.redanzCore.service.email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import javax.mail.Session;
import java.util.Properties;

@Configuration
public class CustomMailConfig {

  @Primary
  @Bean
  public FreeMarkerConfigurationFactoryBean factoryBean() {
    FreeMarkerConfigurationFactoryBean bean =new FreeMarkerConfigurationFactoryBean();
    bean.setTemplateLoaderPath("classpath:/templates");
    return bean;
  }
}
