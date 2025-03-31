package talento.futuro.iotapidev;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import talento.futuro.iotapidev.config.DataSourceConfig;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@ActiveProfiles("test")
class IotApiDevApplicationTests {

    @Test
    void contextLoads() {
    }

}
