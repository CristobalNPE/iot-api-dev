package talento.futuro.iotapidev.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import talento.futuro.iotapidev.model.Company;
import talento.futuro.iotapidev.repository.CompanyRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyApiKeyFilter extends OncePerRequestFilter {

    private static final String COMPANY_API_KEY = "X-Company-Api-Key";
    private static final String COMPANY_API_KEY_PARAM = "company_api_key";
    private final CompanyRepository companyRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        if (path.startsWith("/api/v1/admin")) {
            return true;
        }

        if (path.equals("/api/v1/sensor_data") && method.equals("POST")) {
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String companyApiKey = getCompanyApiKey(request);

        if (companyApiKey == null) {
            log.info("Company API Key not found.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Company API Key not found.");
            return;
        }

        Optional<Company> companyOpt = companyRepository.findByApiKey(companyApiKey);

        if (companyOpt.isEmpty()) {
            log.info("Company with API Key {} not found in the DB.", companyApiKey);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Company API Key not found.");
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(
                new ApiKeyAuthentication(
                        companyOpt.get().getId(),
                        companyApiKey,
                        true,
                        List.of(new SimpleGrantedAuthority("ROLE_COMPANY"))
                )
        );

        filterChain.doFilter(request, response);
    }

    private static String getCompanyApiKey(HttpServletRequest request) {
        String companyApiKey = request.getHeader(COMPANY_API_KEY);
        if (companyApiKey == null) {
            companyApiKey = request.getParameter(COMPANY_API_KEY_PARAM);
        }
        return companyApiKey;
    }
}
