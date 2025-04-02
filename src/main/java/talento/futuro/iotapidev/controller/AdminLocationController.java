package talento.futuro.iotapidev.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import talento.futuro.iotapidev.constants.ApiBase;
import talento.futuro.iotapidev.constants.ApiPath;
import talento.futuro.iotapidev.dto.LocationAdminRequest;
import talento.futuro.iotapidev.dto.LocationResponse;
import talento.futuro.iotapidev.service.AdminLocationService;

@RestController
@RequestMapping(ApiBase.V1 + ApiPath.ADMIN + ApiPath.LOCATION)
@RequiredArgsConstructor
public class AdminLocationController {
    private final AdminLocationService adminLocationService;

    @GetMapping
    public Page<LocationResponse> getAllLocations(Pageable pageable) {
        return adminLocationService.findAllLocations(pageable); 
    }

    @GetMapping("/{locationId}")
    public LocationResponse getLocationById(@PathVariable(name = "locationId") Integer locationId) {
        return adminLocationService.findLocationById(locationId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationResponse createLocation(@RequestBody @Valid LocationAdminRequest request) {
        return adminLocationService.createLocation(request);
    }

    @PutMapping("/{locationId}")
    public LocationResponse updateLocation(@RequestBody @Valid LocationAdminRequest request,
                                           @PathVariable(name = "locationId") Integer locationId) {
        return adminLocationService.updateLocation(locationId, request);
    }

    @DeleteMapping("/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocation(@PathVariable(name = "locationId") Integer locationId) {
        adminLocationService.deleteLocation(locationId);
    }
}
