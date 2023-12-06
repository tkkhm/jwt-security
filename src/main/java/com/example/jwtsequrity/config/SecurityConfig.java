package com.example.jwtsequrity.config;


import com.example.jwtsequrity.security.AuthEntryPoint;
import com.example.jwtsequrity.security.AuthJwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@ComponentScan(basePackages = "com.example.jwtsequrity")
public class SecurityConfig {
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthJwtFilter jwtFilter, AuthEntryPoint entryPoint) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/home").permitAll();
                    auth.requestMatchers( "/users/**").hasRole("ADMIN");
                    auth.requestMatchers("/register/**").permitAll();
                    auth.requestMatchers("/login/**").permitAll();
                    auth.requestMatchers("/logout/**").permitAll();
                    auth.requestMatchers("/error/**").permitAll();
                    auth.requestMatchers("/secured").authenticated();
                    auth.anyRequest().authenticated();
                })
                .oauth2Login(withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(entryPoint))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(NO_CONTENT))
                        .deleteCookies("jwt"));
        return http.build();
    }
}
