package talento.futuro.iotapidev.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import talento.futuro.iotapidev.constants.ApiBase;
import talento.futuro.iotapidev.constants.ApiPath;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CompanyApiKeyFilter companyApiKeyFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Disable CORS (no authorized client origins set yet)
        http.cors(AbstractHttpConfigurer::disable);

        // Disable CSRF protection (REST API doesn't require CSRF tokens)
        http.csrf(AbstractHttpConfigurer::disable);

        // Use stateless session management (for REST API)
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Define authorization rules for specific API paths
        http.authorizeHttpRequests(authz -> authz
                // Admin endpoints require "ADMIN" role
                .requestMatchers(ApiBase.V1 + ApiPath.ADMIN + "/**").hasRole("ADMIN")
                // Other endpoints require authentication
                .anyRequest().authenticated()
        );

        // Add Company API key filter before Basic Authentication filter
        http.addFilterBefore(companyApiKeyFilter, BasicAuthenticationFilter.class);

        // Admin endpoints use HTTP Basic Authentication
        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
