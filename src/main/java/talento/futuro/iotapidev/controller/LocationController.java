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
@RequestMapping("/api/v1/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public List<LocationResponse> getAllLocationsForCurrentCompany() {
        return locationService.findAllLocationsForCurrentCompany();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationResponse createLocationForCurrentCompany(@RequestBody @Valid LocationRequest request) {
        return locationService.createLocationForCurrentCompany(request);
    }


}
