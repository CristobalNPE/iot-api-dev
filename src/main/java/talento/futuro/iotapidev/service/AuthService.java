package talento.futuro.iotapidev.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import talento.futuro.iotapidev.exception.InvalidAuthenticationException;
import talento.futuro.iotapidev.security.ApiKeyAuthentication;

@Slf4j
@Service
@Transactional(readOnly = true)
public class AuthService {

    public Integer getCompanyIdFromContext() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof ApiKeyAuthentication) {
            return (Integer) auth.getDetails();
        }
        log.warn("🔴🔴🔴 Invalid authentication type");
        throw new InvalidAuthenticationException("Invalid authentication type");
    }
}