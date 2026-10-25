package ch.redanz.redanzCore.web.security.config;

import jakarta.servlet.DispatcherType;
import org.springframework.http.HttpMethod;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.service.log.ErrorLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
@Slf4j
public class WebSecurityConfig {
  private final UserService userService;
  private final RegistrationService registrationService;
  private final PersonService personService;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final ErrorLogService errorLogService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager(), userService);
    customAuthenticationFilter.setFilterProcessesUrl("/core-api/login");
    http.authorizeHttpRequests(requests -> requests
      // error pages of a request (before the upgrade not checked again)
      .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
      .requestMatchers("/core-api/profile/user/registration/**").permitAll()
      .requestMatchers("/core-api/zahls/checkout/**").permitAll()
      .requestMatchers("/core-api/payrexx/checkout/**").permitAll()
      .requestMatchers("/core-api/profile/**").permitAll()
      .requestMatchers("/core-api/login/check-server").permitAll()
      .requestMatchers("/core-api/login/host-key").permitAll()
      // email accounts (smtp/imap credentials): organizers may view, only admins may change or test
      .requestMatchers(HttpMethod.GET, "/core-api/app/email/accounts", "/core-api/app/email/accounts/testers").hasAnyAuthority("ADMIN", "ORGANIZER")
      .requestMatchers("/core-api/app/email/accounts/**", "/core-api/app/email/accounts").hasAuthority("ADMIN")
      .anyRequest().authenticated());
    http.httpBasic(Customizer.withDefaults());
    http.cors(Customizer.withDefaults());
    http.csrf(AbstractHttpConfigurer::disable);
    http.addFilter(customAuthenticationFilter);
    http.addFilterBefore(new CustomAuthorizationFilter(userService, registrationService, personService, errorLogService), UsernamePasswordAuthenticationFilter.class);
    http.formLogin(formLogin -> formLogin.failureHandler(authenticationFailureHandler()));
    return http.build();
  }

  @Bean
  public AuthenticationFailureHandler authenticationFailureHandler() {
    return new CustomAuthenticationFailureHandler();
  }

  // users from the database, passwords with bcrypt
  @Bean
  public AuthenticationManager authenticationManager() {
    return new ProviderManager(daoAuthenticationProvider());
  }

  private DaoAuthenticationProvider daoAuthenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userService);
    provider.setPasswordEncoder(bCryptPasswordEncoder);
    return provider;
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowCredentials(true);
    configuration.setAllowedOrigins(Arrays.asList("https://registration.stirit.ch", "https://model.redanz.ch", "https://redanz.ch", "https://registration.queerswingzurich.com"));
    configuration.addAllowedOriginPattern(CorsConfiguration.ALL);
    configuration.setAllowedMethods(List.of(CorsConfiguration.ALL));
    configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

}
