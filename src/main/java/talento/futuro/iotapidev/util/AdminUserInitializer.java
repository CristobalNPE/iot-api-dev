package talento.futuro.iotapidev.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import talento.futuro.iotapidev.model.Admin;
import talento.futuro.iotapidev.repository.AdminRepository;

@Component
@RequiredArgsConstructor
public class AdminUserInitializer {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createDefaultAdminUser() {

        if (adminRepository.count() > 0) {
            return;
        }
        adminRepository.save(new Admin(
                null,
                "admin",
                passwordEncoder.encode("admin"),
                "ROLE_ADMIN"
        ));
    }
}
