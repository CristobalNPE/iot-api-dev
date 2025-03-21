package talento.futuro.iotapidev.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import talento.futuro.iotapidev.dto.LocationRequest;
import talento.futuro.iotapidev.dto.LocationResponse;
import talento.futuro.iotapidev.exception.CompanyNotFoundException;
import talento.futuro.iotapidev.exception.DuplicatedLocationException;
import talento.futuro.iotapidev.mapper.LocationMapper;
import talento.futuro.iotapidev.model.Company;
import talento.futuro.iotapidev.model.Location;
import talento.futuro.iotapidev.repository.CompanyRepository;
import talento.futuro.iotapidev.repository.LocationRepository;
import talento.futuro.iotapidev.security.ApiKeyAuthentication;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final CompanyRepository companyRepository;

    public List<LocationResponse> findAllLocationsForCurrentCompany() {

        Integer companyId = getCompanyIdFromContext();

        List<Location> companyLocations = locationRepository.findAllByCompanyId(companyId);

        return companyLocations.stream()
                               .map(locationMapper::toLocationResponse)
                               .toList();
    }

    public LocationResponse createLocationForCurrentCompany(@Valid LocationRequest request) {
        validateRequest(request);

        Integer companyId = getCompanyIdFromContext();

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

    private void validateRequest(@Valid LocationRequest request) {

        if (locationRepository.existsByName(request.name())) {
            throw new DuplicatedLocationException(request.name());
        }


    }


    private Integer getCompanyIdFromContext() {
        ApiKeyAuthentication authentication =
                (ApiKeyAuthentication) SecurityContextHolder.getContext().getAuthentication();

        return (Integer) authentication.getDetails();
    }
}
