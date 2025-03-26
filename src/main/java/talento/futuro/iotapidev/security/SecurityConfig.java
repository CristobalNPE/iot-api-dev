package talento.futuro.iotapidev.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
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
        // Temporarily disable CORS (no authorized client origins configured yet)
        http.cors(AbstractHttpConfigurer::disable);

        // Disable CSRF protection (allow state-changing requests without CSRF token)
        http.csrf(AbstractHttpConfigurer::disable);

        // Use stateless session management (for RESTful API)
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Define authorization rules
        http.authorizeHttpRequests(authz -> authz
                // Restrict access to admin endpoints
                .requestMatchers(ApiBase.V1 + ApiPath.ADMIN + "/**").hasRole("ADMIN")
                // Require authentication for all other requests
                .anyRequest().authenticated()
        );

        // Add Company API key filter before Basic Authentication filter
        http.addFilterBefore(companyApiKeyFilter, BasicAuthenticationFilter.class);

        // Enable HTTP Basic Authentication
        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public FilterRegistrationBean<CompanyApiKeyFilter> companyApiKeyFilterRegistration(CompanyApiKeyFilter filter) {
        FilterRegistrationBean<CompanyApiKeyFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
