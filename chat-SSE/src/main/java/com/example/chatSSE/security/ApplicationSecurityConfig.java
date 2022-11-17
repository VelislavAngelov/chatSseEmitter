package com.example.chatSSE.security;

import static com.example.chatSSE.security.ApplicationUserRole.ADMIN;

import com.example.chatSSE.model.User;
import com.example.chatSSE.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter

{
  private final PasswordEncoder passwordEncoder;
  private final UserRepository  userRepository;

  @Autowired
  public ApplicationSecurityConfig(
      PasswordEncoder passwordEncoder, UserRepository userRepository)
  {
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception
  {
    http
        .csrf()
        .disable()
        .authorizeHttpRequests()
        .antMatchers(HttpMethod.POST, "/api/v1/user/registration").permitAll()
        .antMatchers(HttpMethod.DELETE,"/api/v1/user/userById/{id}").hasRole(ADMIN.name())
        .antMatchers(HttpMethod.GET,"/api/v1/user/allUsers").hasRole(ADMIN.name())
        .antMatchers("/api/v1/message/**").authenticated()
        .anyRequest()
        .authenticated()
        .and()
        .httpBasic();
  }

  @Override
  @Bean
  protected UserDetailsService userDetailsService()
  {
    List<User> users = userRepository.readAllUsers();
    List<UserDetails> returnUsers = new ArrayList<>();
    for (User user : users) {
      returnUsers
          .add(
              org.springframework.security.core.userdetails.User
                  .builder()
                  .username(user.getUserName())
                  .password(passwordEncoder.encode(user.getPassword()))
                  .roles(ApplicationUserRole.USER.name())
                  .build());
    }

    UserDetails admin = org.springframework.security.core.userdetails.User
        .builder()
        .username("Linda")
        .password(passwordEncoder.encode("password"))
        .roles(ADMIN.name())
        .build();

    returnUsers.add(admin);

    return new InMemoryUserDetailsManager(returnUsers);
  }
}