package talento.futuro.iotapidev.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import talento.futuro.iotapidev.security.ApiKeyAuthentication;

@Service
@Transactional(readOnly = true)
public class AuthService {

    public Integer getCompanyIdFromContext() {
        ApiKeyAuthentication authentication =
                (ApiKeyAuthentication) SecurityContextHolder.getContext().getAuthentication();

        return (Integer) authentication.getDetails();
    }

}
