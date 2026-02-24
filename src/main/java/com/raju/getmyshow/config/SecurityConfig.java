package com.raju.getmyshow.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
@Configuration


public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Allow all requests temporarily
                )
                .formLogin(form -> form.disable()) // Disable default login page
                .httpBasic(basic -> basic.disable()); // Disable basic auth
        return http.build();
    }
}
