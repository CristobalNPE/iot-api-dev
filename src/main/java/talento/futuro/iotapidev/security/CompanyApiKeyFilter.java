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
import talento.futuro.iotapidev.constants.ApiBase;
import talento.futuro.iotapidev.constants.ApiPath;
import talento.futuro.iotapidev.model.Company;
import talento.futuro.iotapidev.repository.CompanyRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static talento.futuro.iotapidev.constants.ApiKeys.COMPANY_API_KEY_HEADER;
import static talento.futuro.iotapidev.constants.ApiKeys.COMPANY_API_KEY_PARAM;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyApiKeyFilter extends OncePerRequestFilter {

    private final CompanyRepository companyRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String path = request.getRequestURI();
        String method = request.getMethod();
        log.debug("shouldNotFilter Check: Path='{}', Method='{}'", path, method);

        if (path.startsWith(ApiBase.V1 + ApiPath.ADMIN)) {
            return true;
        }

        if (path.equals(ApiBase.V1 + ApiPath.SENSOR_DATA) &&  "POST".equalsIgnoreCase(method)) {
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String companyApiKey = getCompanyApiKey(request);

        if (companyApiKey == null || companyApiKey.isBlank()) {
            log.info("Company API Key not found.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Company API Key not found.");
            return;
        }

        Optional<Company> companyOpt = companyRepository.findByApiKey(companyApiKey);

        if (companyOpt.isEmpty()) {
            log.info("Company with API Key {} not found in the DB.", companyApiKey);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Company API Key not found.");
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
        String companyApiKey = request.getHeader(COMPANY_API_KEY_HEADER);
        if (companyApiKey == null) {
            companyApiKey = request.getParameter(COMPANY_API_KEY_PARAM);
        }
        return companyApiKey;
    }

}
