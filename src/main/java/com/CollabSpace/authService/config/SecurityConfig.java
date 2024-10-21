package com.CollabSpace.authService.config;



import com.CollabSpace.authService.security.JWTAuthenticationFilter;
import com.CollabSpace.authService.security.JwtAuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JWTAuthenticationFilter filter;
    @Autowired
    private JwtAuthenticationEntryPoint entryPoint;



    //    SecurityFilterChain beans
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        //configurations

        //urls
        //public koun se protected
        //koun se urls admin, koun se normal user.


        //configuring urls
        //cors ko hame abhi ke lie disable kiy hai
        //isko ham log baad mein sikhenge
//        security.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.disable());
        //csrf ko hame abhi ke lie disable kiy hai

        security.cors(httpSecurityCorsConfigurer ->
                        httpSecurityCorsConfigurer.configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration corsConfiguration = new CorsConfiguration();

                                //origins
                                //methods
//                        corsConfiguration.addAllowedOrigin("http://localhost:4200");

//                                =corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:4300", "http://localhost:4300"));
                                corsConfiguration.setAllowedOriginPatterns(List.of("*"));
                                corsConfiguration.setAllowedMethods(List.of("*"));
                                corsConfiguration.setAllowCredentials(true);
                                corsConfiguration.setAllowedHeaders(List.of("*"));
                                corsConfiguration.setMaxAge(4000L);


                                return corsConfiguration;
                            }
                        })
        );

        //isko ham log baad mein sikhenge
        security.csrf(AbstractHttpConfigurer::disable);

        security.authorizeHttpRequests(request ->
                request.requestMatchers(HttpMethod.DELETE, "/users/**").hasRole(AppConstants.ROLE_ADMIN)
                       // requestMatchers(HttpMethod.PUT, "/users/**").hasAnyRole(AppConstants.ROLE_ADMIN, AppConstants.ROLE_NORMAL)
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                        //.requestMatchers("/api/**").hasRole(AppConstants.ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET, "/users/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                        .requestMatchers("/categories/**").hasRole(AppConstants.ROLE_ADMIN)
                        .requestMatchers(HttpMethod.POST, "/auth/generate-token", "/auth/login-with-google", "/auth/regenerate-token").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/users/**").permitAll()
                        .requestMatchers("/auth/**").authenticated()
                        .anyRequest().permitAll()


        );

        //kis type ki security:
//        security.httpBasic(Customizer.withDefaults());

        //entry point
        security.exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint));

        //session creation policy
        security.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //main -->
        security.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return security.build();

    }


    //    password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager();
    }

}

