package com.springboot.authentication.config;

import com.springboot.authentication.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        private final UserService userService;
        private final CustomLoginSuccessHandler customLoginSuccessHandler;

        public SecurityConfig(
                        UserService userService,
                        CustomLoginSuccessHandler customLoginSuccessHandler) {
                this.userService = userService;
                this.customLoginSuccessHandler = customLoginSuccessHandler;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .csrf(csrf -> csrf.disable())

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/",
                                                                "/login",
                                                                "/register",
                                                                "/register/confirmToken",
                                                                "/forgot-password",
                                                                "/reset-password",
                                                                "/reset-password/**",
                                                                "/css/**",
                                                                "/js/**",
                                                                "/images/**",
                                                                "/assets/**",
                                                                "/webjars/**")
                                                .permitAll()

                                                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                                                .requestMatchers("/user/**").hasAuthority("USER")
                                                .requestMatchers("/file/**").authenticated()

                                                .anyRequest().authenticated())

                                .formLogin(login -> login
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .usernameParameter("username")
                                                .passwordParameter("password")
                                                .successHandler(customLoginSuccessHandler)
                                                .failureUrl("/login?error=true")
                                                .permitAll())

                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login")
                                                .invalidateHttpSession(true)
                                                .clearAuthentication(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll())

                                .userDetailsService(userService)

                                .build();
        }

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration authenticationConfiguration) throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }
}