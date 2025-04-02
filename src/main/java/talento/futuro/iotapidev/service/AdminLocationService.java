package talento.futuro.iotapidev.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import talento.futuro.iotapidev.dto.LocationAdminRequest;
import talento.futuro.iotapidev.dto.LocationResponse;
import talento.futuro.iotapidev.exception.CompanyNotFoundException;
import talento.futuro.iotapidev.exception.LocationNotFoundException;
import talento.futuro.iotapidev.mapper.LocationMapper;
import talento.futuro.iotapidev.model.Company;
import talento.futuro.iotapidev.model.Location;
import talento.futuro.iotapidev.repository.CompanyRepository;
import talento.futuro.iotapidev.repository.LocationRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminLocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final CompanyRepository companyRepository;

     public Page<LocationResponse> findAllLocations(Pageable pageable) {
        Page<Location> locations = locationRepository.findAll(pageable);

        return locations.map(locationMapper::toLocationResponse);
    }

    public LocationResponse createLocation(@Valid LocationAdminRequest request) {
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

    public LocationResponse findLocationById(Integer locationId) {
        return locationRepository.findById(locationId)
                                 .map(locationMapper::toLocationResponse)
                                 .orElseThrow(() -> new LocationNotFoundException(locationId));
    }

    public LocationResponse updateLocation(Integer locationId, @Valid LocationAdminRequest request) {
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

    public void deleteLocation(Integer locationId) {
        if (!locationRepository.existsById(locationId)) {
            throw new LocationNotFoundException(locationId);
        }
        locationRepository.deleteById(locationId);
    }
}
