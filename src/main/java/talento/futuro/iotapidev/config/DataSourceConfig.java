package talento.futuro.iotapidev.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
@RequiredArgsConstructor
@Profile("!test")
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(convertPostgresUrlToJdbc(datasourceUrl));
        return new HikariDataSource(config);
    }

    public static String convertPostgresUrlToJdbc(String postgresUrl) {
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
