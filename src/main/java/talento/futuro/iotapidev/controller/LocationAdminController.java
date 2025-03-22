package talento.futuro.iotapidev.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import talento.futuro.iotapidev.dto.LocationRequest;
import talento.futuro.iotapidev.dto.LocationResponse;
import talento.futuro.iotapidev.service.LocationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/location")
@RequiredArgsConstructor
public class LocationAdminController {
    private final LocationService locationService;

    @GetMapping
    public List<LocationResponse> getAllLocations() {
        return locationService.adminFindAllLocations();
    }

    @GetMapping("/{locationId}")
    public LocationResponse getLocationById(@PathVariable(name = "locationId") Integer locationId) {
        return locationService.adminFindLocationById(locationId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationResponse createLocation(@RequestBody @Valid LocationRequest request) {
        return locationService.adminCreateLocation(request);
    }

    @PutMapping("/{locationId}")
    public LocationResponse updateLocation(@RequestBody @Valid LocationRequest request, @PathVariable(name = "locationId") Integer locationId) {
        return locationService.adminUpdateLocation(locationId, request);
    }

    @DeleteMapping("/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocation(@PathVariable(name = "locationId") Integer locationId) {
        locationService.adminDeleteLocation(locationId);
    }
}
