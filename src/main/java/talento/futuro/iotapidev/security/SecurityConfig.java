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

        return http.cors(AbstractHttpConfigurer::disable)
                   .csrf(AbstractHttpConfigurer::disable)
                   .sessionManagement(session -> session
                           .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                   .authorizeHttpRequests(authz -> authz
                           .requestMatchers(ApiBase.V1 + ApiPath.ADMIN + "/**").hasRole("ADMIN")
                           .anyRequest().authenticated()
                   )
                   .addFilterBefore(companyApiKeyFilter, BasicAuthenticationFilter.class)
                   .httpBasic(Customizer.withDefaults())
                   .build();
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
