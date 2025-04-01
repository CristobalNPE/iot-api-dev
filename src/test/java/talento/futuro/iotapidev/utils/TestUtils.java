package talento.futuro.iotapidev.utils;

import java.util.UUID;

public class TestUtils {
    public static String generateApiKeyForTests() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
