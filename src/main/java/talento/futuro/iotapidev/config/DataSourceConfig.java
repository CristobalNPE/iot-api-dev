package talento.futuro.iotapidev.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@PropertySource("classpath:database.properties")
@ComponentScan("talento.futuro.iotapidev")
public class DataSourceConfig {

    private final Environment environment;

    @Bean
    DataSource dataSource() {
        DriverManagerDataSource dmds = new DriverManagerDataSource();
        dmds.setDriverClassName(environment.getProperty("driver"));
        dmds.setUrl(environment.getProperty("url"));
        dmds.setUsername(environment.getProperty("dbUser"));
        dmds.setPassword(environment.getProperty("dbPassword"));
        return dmds;
    }



}
