package talento.futuro.iotapidev.validators;

import java.time.Instant;

public class TimestampValidator {

    /**
     * Validates whether the given long value can be converted into a valid Unix timestamp (Instant).
     * @param timestamp The timestamp (Unix time) to validate.
     * @return true if the timestamp is valid, false otherwise.
     */
    public static boolean isValidTimestamp(long timestamp) {
        try {
            Instant.ofEpochSecond(timestamp);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
