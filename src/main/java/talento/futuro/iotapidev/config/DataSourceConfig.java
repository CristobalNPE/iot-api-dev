package talento.futuro.iotapidev.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@PropertySource("classpath:database.properties")
@Profile("!test")
public class DataSourceConfig {

    private final Environment environment;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(environment.getRequiredProperty("driver"));
        config.setJdbcUrl(environment.getRequiredProperty("url"));
        config.setUsername(environment.getRequiredProperty("dbUser"));
        config.setPassword(environment.getRequiredProperty("dbPassword"));
        return new HikariDataSource(config);
    }
}
