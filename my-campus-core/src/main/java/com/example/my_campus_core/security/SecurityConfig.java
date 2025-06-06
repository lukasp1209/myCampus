package com.example.my_campus_core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.my_campus_core.models.UserEntity;
import com.example.my_campus_core.repository.UserRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
        private CustomUserDetailsService userDetailsService;
        private UserRepository userRepository;

        @Autowired
        public SecurityConfig(CustomUserDetailsService userDetailsService, UserRepository userRepository) {
                this.userDetailsService = userDetailsService;
                this.userRepository = userRepository;
        }

        @SuppressWarnings("deprecation")
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeRequests(requests -> requests
                                                .requestMatchers(
                                                                // "/user/register",
                                                                "/login",
                                                                "/setup",
                                                                "/api/**",
                                                                // "/api/user/v1/**",
                                                                // "/api/user/v1/info",
                                                                "/css/**",
                                                                "/js/**",
                                                                "/product/**",
                                                                "/static/**")
                                                .permitAll()
                                                .requestMatchers(
                                                                "/course/managment",
                                                                "/course/add")
                                                .hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                // .defaultSuccessUrl("/dashboard", true)
                                                .loginProcessingUrl("/login")
                                                .failureUrl("/login?error=true")
                                                .permitAll())
                                .logout(
                                                logout -> logout
                                                                .logoutRequestMatcher(
                                                                                new AntPathRequestMatcher("/logout"))
                                                                .permitAll());

                return http
                                .formLogin(login -> login
                                                .successHandler((request, response, authentication) -> {
                                                        Authentication auth = SecurityContextHolder.getContext()
                                                                        .getAuthentication();
                                                        String currentUser = auth.getName();
                                                        UserEntity user = userRepository.findByEmail(currentUser);
                                                        // // Check the role of the authenticated user
                                                        // if (auth.getAuthorities().contains(new
                                                        // SimpleGrantedAuthority("ROLE_ADMIN")))
                                                        // {
                                                        // // If the user has the role ADMIN, redirect to /dashboard
                                                        // UserEntity id = user;
                                                        // response.sendRedirect("/dashboard/" + id.getId());
                                                        // } else if (auth.getAuthorities().contains(new
                                                        // SimpleGrantedAuthority("ROLE_USER"))) {
                                                        // // If the user has the role USER, redirect to /shop
                                                        // response.sendRedirect("/");
                                                        // }
                                                        response.sendRedirect("/");
                                                }))
                                .build();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }

        @Bean
        PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        public void configure(AuthenticationManagerBuilder builder) throws Exception {
                builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        }
}