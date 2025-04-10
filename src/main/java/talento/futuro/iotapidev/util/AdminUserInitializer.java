package talento.futuro.iotapidev.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import talento.futuro.iotapidev.model.Admin;
import talento.futuro.iotapidev.repository.AdminRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminUserInitializer {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.admin.username}")
    private String adminUsername;

    @Value("${app.security.admin.password}")
    private String adminPassword;


    @PostConstruct
    public void createDefaultAdminUser() {

        if (!StringUtils.hasText(adminUsername) || !StringUtils.hasText(adminPassword)) {
            log.warn("⚠ Admin username or password not set on ENV. Admin user will not be created.");
            return;
        }

        if (adminRepository.count() > 0) {
            log.info("👤 Admin user found. Skipping creation.");
            return;
        }
        log.info("👤 Creating default admin user '{}'", adminUsername);
        adminRepository.save(new Admin(
                null,
                adminUsername,
                passwordEncoder.encode(adminPassword),
                "ROLE_ADMIN"
        ));
    }
}
