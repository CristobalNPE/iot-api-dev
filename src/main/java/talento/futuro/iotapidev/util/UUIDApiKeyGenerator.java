package talento.futuro.iotapidev.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDApiKeyGenerator implements ApiKeyGenerator {

    @Override
    public String generateApiKey() {
        return UUID.randomUUID()
                   .toString()
                   .replace("-", "");
    }
}
