package talento.futuro.iotapidev.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.URI;

@Slf4j
@Configuration
@Profile("!test")
@ConfigurationProperties(prefix = "app.datasource")
@Setter
public class DataSourceConfig {

    private String databaseUrl;
    private String databaseUsername;
    private String databasePassword;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        String urlFromEnv = System.getenv("DATABASE_URL");
        String effectiveDbUrl;

        if (StringUtils.hasText(urlFromEnv)) {
            log.info("🔎 Database URL loaded from environment variable");
            effectiveDbUrl = urlFromEnv;

            if (!effectiveDbUrl.startsWith("jdbc:")) {
                try {
                    effectiveDbUrl = convertPostgresUrlToJdbc(effectiveDbUrl);
                    log.info("🔁 Converted Postgres URL ({}) to JDBC URL -> {}", urlFromEnv, effectiveDbUrl);
                } catch (Exception e) {
                    log.error("🚫 Error converting DATABASE_URL", e);
                    throw new RuntimeException("Invalid DATABASE_URL format");
                }
            }
        } else {
            log.info("🍃 Using DB config from application.properties");
            effectiveDbUrl = databaseUrl;
        }


        config.setUsername(databaseUsername);
        config.setPassword(databasePassword);
        config.setJdbcUrl(effectiveDbUrl);
        config.setDriverClassName("org.postgresql.Driver");

        log.info("✅ Database connection configured with URL: {}",
                effectiveDbUrl.replaceAll("password=\\w+", "password=****"));

        return new HikariDataSource(config);
    }

    private String convertPostgresUrlToJdbc(String postgresUrl) {

        URI uri = URI.create(postgresUrl);
        String host = uri.getHost();
        int port = uri.getPort();
        String path = uri.getPath();
        String[] userInfo = uri.getUserInfo().split(":");

        return String.format("jdbc:postgresql://%s:%d%s?user=%s&password=%s&sslmode=disable",
                host,
                port,
                path,
                userInfo[0],
                userInfo[1]
        );
    }
}
