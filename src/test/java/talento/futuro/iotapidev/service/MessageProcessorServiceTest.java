package talento.futuro.iotapidev.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class MessageProcessorServiceTest {

    @Autowired
    private MessageProcessorService messageProcessorService;

    @Test
    public void testProcessMessage() {
        String jsonMessage = """
                {"sensor_api_key":"82ba1908-96c7-4a7b-854c-969a5e389909","json_data":[{"datetime":1742860430,"temp":24.4,"humidity":0.5}]}
                """;

        assertDoesNotThrow(() -> messageProcessorService.processMessage(jsonMessage));
    }

}
