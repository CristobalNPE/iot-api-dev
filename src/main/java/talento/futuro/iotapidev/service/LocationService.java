package talento.futuro.iotapidev.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import talento.futuro.iotapidev.dto.LocationRequest;
import talento.futuro.iotapidev.dto.LocationResponse;
import talento.futuro.iotapidev.exception.DuplicatedException;
import talento.futuro.iotapidev.exception.NotFoundException;
import talento.futuro.iotapidev.mapper.LocationMapper;
import talento.futuro.iotapidev.model.Company;
import talento.futuro.iotapidev.model.Location;
import talento.futuro.iotapidev.repository.CompanyRepository;
import talento.futuro.iotapidev.repository.LocationRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final CompanyRepository companyRepository;
    private final AuthService authService;

    public Page<LocationResponse> findAllLocationsForCurrentCompany(Pageable pageable) {

        Integer companyId = authService.getCompanyIdFromContext();

        Page<Location> companyLocationsPage = locationRepository.findAllByCompanyId(companyId, pageable);

        return companyLocationsPage.map(locationMapper::toLocationResponse);
    }

    public LocationResponse createLocationForCurrentCompany(@Valid LocationRequest request) {
        validateRequest(request);

        Integer companyId = authService.getCompanyIdFromContext();

        Company company = companyRepository.findById(companyId).orElseThrow(
                () -> new NotFoundException("Company",companyId));

        Location newLocation = Location.builder()
                                       .name(request.name())
                                       .city(request.city())
                                       .meta(request.meta())
                                       .country(request.country())
                                       .build();

        newLocation.setCompany(company);
        Location saved = locationRepository.save(newLocation);

        return locationMapper.toLocationResponse(saved);
    }

    public LocationResponse updateLocationForCompany(Integer locationId, @Valid LocationRequest request) {

        Location location = getLocationForCompany(locationId);

        location.setName(request.name());
        location.setCity(request.city());
        location.setCountry(request.country());
        location.setMeta(request.meta());

        return locationMapper.toLocationResponse(locationRepository.save(location));
    }

    public void deleteLocationForCompany(Integer locationId) {
        Location location = getLocationForCompany(locationId);
        locationRepository.deleteById(location.getId());
    }

    public LocationResponse findLocationByIdForCompany(Integer locationId) {
        Location location = getLocationForCompany(locationId);
        return locationMapper.toLocationResponse(location);
    }

    public Location getLocationForCompany(Integer locationId) {

        Integer companyId = authService.getCompanyIdFromContext();

        return locationRepository.findByIdAndCompanyId(locationId, companyId)
                                 .orElseThrow(() -> new NotFoundException("Location",locationId));
    }

    private void validateRequest(@Valid LocationRequest request) {
        if (locationRepository.existsByNameForCompany(request.name(), authService.getCompanyIdFromContext())) {
            throw new DuplicatedException("Location", request.name());
        }
    }
}
