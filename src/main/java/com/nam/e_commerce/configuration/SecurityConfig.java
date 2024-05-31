package com.nam.e_commerce.configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import com.nam.e_commerce.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Autowired private GoogleOAuth2LoginSuccessHandler googleOAuth2LoginSuccessHandler;

  @Autowired private CustomUserDetailService customUserDetailService;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            (authz) ->
                authz
                    .requestMatchers("/", "/shop/**", "/register", "/h2-console/**")
                    .permitAll()
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated())
        .formLogin(
            config ->
                config
                    .loginPage("/login")
                    .permitAll()
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/login?error=true"))
        .oauth2Login(
            config -> config.loginPage("/login").successHandler(googleOAuth2LoginSuccessHandler))
        .logout(
            config ->
                config
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login")
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID"))
        .exceptionHandling(withDefaults())
        .httpBasic(withDefaults());
    http.csrf(AbstractHttpConfigurer::disable);
    http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));
    //    http.authenticationProvider(authenticationProvider());
//        http.securityMatcher(
//            "/resources/**", "/static/**", "/css/**", "/js/**", "/images/**", "productImages/**");
    return http.build();
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring()
            .requestMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**", "/productImages/**");
  }
}
