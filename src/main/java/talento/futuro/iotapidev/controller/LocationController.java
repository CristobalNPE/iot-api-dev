package talento.futuro.iotapidev.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import talento.futuro.iotapidev.constants.ApiBase;
import talento.futuro.iotapidev.constants.ApiPath;
import talento.futuro.iotapidev.dto.LocationRequest;
import talento.futuro.iotapidev.dto.LocationResponse;
import talento.futuro.iotapidev.service.LocationService;

import java.util.List;

@RestController
@RequestMapping(ApiBase.V1 + ApiPath.LOCATION)
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public List<LocationResponse> getAllLocationsForCurrentCompany() {
        return locationService.findAllLocationsForCurrentCompany();
    }

    @GetMapping("/{locationId}")
    public LocationResponse getLocationById(@PathVariable(name = "locationId") Integer locationId) {
        return locationService.findLocationByIdForCompany(locationId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationResponse createLocationForCurrentCompany(@RequestBody @Valid LocationRequest request) {

        return locationService.createLocationForCurrentCompany(request);
    }

    @PutMapping("/{locationId}")
    public LocationResponse updateLocation(@RequestBody @Valid LocationRequest request,
                                           @PathVariable(name = "locationId") Integer locationId) {

        return locationService.updateLocationForCompany(locationId, request);
    }

    @DeleteMapping("/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocation(@PathVariable(name = "locationId") Integer locationId) {
        locationService.deleteLocationForCompany(locationId);

    }
}
