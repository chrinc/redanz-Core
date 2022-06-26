package ch.redanz.redanzCore.web.security.config;

import ch.redanz.redanzCore.model.profile.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  private final UserService userService;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
    customAuthenticationFilter.setFilterProcessesUrl("/core-api/login");
 //   customAuthenticationFilter.setFilterProcessesUrl("/core-api/zahls/checkout/confirm");

//    http.authorizeRequests().antMatchers("/checkout").permitAll();
    http.authorizeRequests().antMatchers("/core-api/profile/user/registration/**").permitAll();
    http.authorizeRequests().antMatchers("/core-api/zahls/checkout/confirm/**").permitAll();
    http.authorizeRequests().antMatchers("/core-api/profile/**").permitAll();
    http.authorizeRequests().antMatchers("/core-api/login/check-server").permitAll();
//    http.authorizeRequests().antMatchers("/login").permitAll();
//    http.authorizeRequests().antMatchers("/**").permitAll();
    http.authorizeRequests().anyRequest().authenticated().and().httpBasic();
    http.cors();
    http.csrf().disable();
//    http.sessionManagement().sessionCreationPolicy(STATELESS);
//    http.authorizeRequests().antMatchers("/api/login/**").permitAll();
//    http.authorizeRequests().antMatchers("/api/token/refresh/**").permitAll();
//    http.authorizeRequests().anyRequest().authenticated();
    http.addFilter(customAuthenticationFilter);
    http.addFilterBefore(new CustomAuthorizationFilter(userService), UsernamePasswordAuthenticationFilter.class);

    http.formLogin().failureHandler(authenticationFailureHandler());
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userService);
//    auth.authenticationProvider(daoAuthenticationProvider());
  }

  @Bean
  public AuthenticationFailureHandler authenticationFailureHandler() {
    return new CustomAuthenticationFailureHandler();
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider() {
    DaoAuthenticationProvider provider =
      new DaoAuthenticationProvider();
    provider.setPasswordEncoder(bCryptPasswordEncoder);
    provider.setUserDetailsService(userService);

    return provider;
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOriginPattern(CorsConfiguration.ALL);
    configuration.setAllowedMethods(List.of(CorsConfiguration.ALL));
    configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
