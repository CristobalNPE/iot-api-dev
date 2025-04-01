package talento.futuro.iotapidev.utils;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import talento.futuro.iotapidev.security.ApiKeyAuthentication;

import java.util.List;

import static talento.futuro.iotapidev.utils.TestUtils.generateApiKeyForTests;

public class WithMockCompanySecurityContextFactory implements WithSecurityContextFactory<WithMockCompany> {


    @Override
    public SecurityContext createSecurityContext(WithMockCompany annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        int companyId = annotation.companyId();

        String apiKey = annotation.apiKey().isEmpty()
                ? generateApiKeyForTests()
                : annotation.apiKey();

        ApiKeyAuthentication auth = new ApiKeyAuthentication(
                companyId,
                apiKey,
                true,
                List.of(new SimpleGrantedAuthority("ROLE_COMPANY"))
        );

        context.setAuthentication(auth);
        return context;
    }
}

