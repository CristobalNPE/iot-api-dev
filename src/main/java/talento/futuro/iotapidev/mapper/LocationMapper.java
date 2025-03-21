package talento.futuro.iotapidev.mapper;

import org.springframework.stereotype.Component;
import talento.futuro.iotapidev.dto.LocationResponse;
import talento.futuro.iotapidev.model.Location;

@Component
public class LocationMapper {

    public LocationResponse toLocationResponse(Location location) {
        return new LocationResponse(
                location.getId(),
                location.getName(),
                location.getCountry(),
                location.getCity(),
                location.getMeta()
        );
    }

}
