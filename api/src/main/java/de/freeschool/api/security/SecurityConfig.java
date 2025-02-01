package de.freeschool.api.security;

import de.freeschool.api.models.type.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private JwtAuthEntryPoint authEntryPoint;
    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Value("${school.url}")
    private String schoolUrl;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(handling -> handling.authenticationEntryPoint(authEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authz) -> authz

                        // auth endpoint: needed for registration
                        .requestMatchers("api/v1/auth/**")
                        .permitAll()

                        // ... but refreshing token only when logged in
                        .requestMatchers("api/v1/auth/refreshToken")
                        .authenticated()

                        // meta endpoint: open for anyone
                        .requestMatchers("api/v1/meta/**")
                        .permitAll()

                        // admin endpoints
                        .requestMatchers("api/v1/blocks/**")
                        .hasAuthority(RoleType.ADMIN.name())
                        .requestMatchers("api/v1/admin/**")
                        .hasAuthority(RoleType.ADMIN.name())

                        // anything else needs authentication
                        .requestMatchers("api/v1/**")
                        .authenticated()

                        // ... except swagger ui
                        .requestMatchers("swagger-ui/**")
                        .permitAll()
                        .requestMatchers("v3/**")
                        .permitAll()  // necessary for swagger-ui

                        .anyRequest()
                        .permitAll())
                .httpBasic(withDefaults());
        http.cors();
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Allow cross-origin requests from dev server
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
                Arrays.asList("http://localhost:5173", "https://" + schoolUrl, "http://localhost:3000",
                        "http://localhost:4173"
                ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/testapi/**", configuration);
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }
}
