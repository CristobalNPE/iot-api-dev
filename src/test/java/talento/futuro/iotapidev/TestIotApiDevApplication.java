package talento.futuro.iotapidev;

import org.springframework.boot.SpringApplication;

public class TestIotApiDevApplication {

    public static void main(String[] args) {
        SpringApplication.from(IotApiDevApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
