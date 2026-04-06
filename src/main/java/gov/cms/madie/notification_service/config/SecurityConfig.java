package gov.cms.madie.notification_service.config;

import gov.cms.madie.notification_service.config.security.SecurityExceptionHandlers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  private static final String[] AUTH_WHITELIST = {
    "/actuator/**",
  };

  @Bean
  protected SecurityFilterChain filterChain(
      HttpSecurity http, SecurityExceptionHandlers securityExceptionHandlers) throws Exception {
    http.cors(withDefaults())
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(AUTH_WHITELIST)
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
        .exceptionHandling(
            ex ->
                ex.accessDeniedHandler(securityExceptionHandlers)
                    .authenticationEntryPoint(securityExceptionHandlers));
    return http.build();
  }
}
