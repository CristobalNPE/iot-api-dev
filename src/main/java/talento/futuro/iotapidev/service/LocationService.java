package talento.futuro.iotapidev.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import talento.futuro.iotapidev.dto.LocationAdminRequest;
import talento.futuro.iotapidev.dto.LocationRequest;
import talento.futuro.iotapidev.dto.LocationResponse;
import talento.futuro.iotapidev.exception.CompanyNotFoundException;
import talento.futuro.iotapidev.exception.DuplicatedLocationException;
import talento.futuro.iotapidev.exception.LocationNotFoundException;
import talento.futuro.iotapidev.mapper.LocationMapper;
import talento.futuro.iotapidev.model.Company;
import talento.futuro.iotapidev.model.Location;
import talento.futuro.iotapidev.repository.CompanyRepository;
import talento.futuro.iotapidev.repository.LocationRepository;

import java.util.List;

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
                () -> new CompanyNotFoundException(companyId));

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
                                 .orElseThrow(() -> new LocationNotFoundException(locationId));
    }

    private void validateRequest(@Valid LocationRequest request) {
        if (locationRepository.existsByName(request.name())) {
            throw new DuplicatedLocationException(request.name());
        }
    }

    public Page<LocationResponse> adminFindAllLocations(Pageable pageable) {
        Page<Location> locations = locationRepository.findAll(pageable);

        return locations.map(locationMapper::toLocationResponse);
    }

    public LocationResponse adminCreateLocation(@Valid LocationAdminRequest request) {
        if (request.companyId() == null) {
            throw new CompanyNotFoundException(null);
        }
        Company company = companyRepository.findById(request.companyId())
                                           .orElseThrow(() -> new CompanyNotFoundException(request.companyId()));

        Location newLocation = Location
                .builder()
                .company(company)
                .name(request.name())
                .city(request.city())
                .country(request.country())
                .meta(request.meta())
                .build();

        Location saved = locationRepository.save(newLocation);

        return locationMapper.toLocationResponse(saved);
    }

    public LocationResponse adminFindLocationById(Integer locationId) {
        return locationRepository.findById(locationId)
                                 .map(locationMapper::toLocationResponse)
                                 .orElseThrow(() -> new LocationNotFoundException(locationId));
    }

    public LocationResponse adminUpdateLocation(Integer locationId, @Valid LocationAdminRequest request) {
        if (request.companyId() == null) {
            throw new CompanyNotFoundException(null);
        }
        Company company = companyRepository.findById(request.companyId())
                                           .orElseThrow(() -> new CompanyNotFoundException(request.companyId()));

        Location location = locationRepository.findById(locationId)
                                              .orElseThrow(() -> new LocationNotFoundException(locationId));

        location.setCompany(company);
        location.setName(request.name());
        location.setCity(request.city());
        location.setCountry(request.country());
        location.setMeta(request.meta());

        return locationMapper.toLocationResponse(locationRepository.save(location));
    }

    public void adminDeleteLocation(Integer locationId) {
        if (!locationRepository.existsById(locationId)) {
            throw new LocationNotFoundException(locationId);
        }
        locationRepository.deleteById(locationId);
    }
}
