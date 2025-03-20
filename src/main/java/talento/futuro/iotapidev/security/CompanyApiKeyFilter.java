package talento.futuro.iotapidev.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CompanyApiKeyFilter extends OncePerRequestFilter {

    public static final String COMPANY_API_KEY = "X-Company-Api-Key";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String companyApiKey = request.getHeader(COMPANY_API_KEY);


    }
}

//{
//  "api_key": <sensor_api_key>,
//  "json_data":[{...}, {....}]
//}