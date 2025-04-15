package talento.futuro.iotapidev.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import talento.futuro.iotapidev.dto.SensorRequest;
import talento.futuro.iotapidev.dto.SensorResponse;
import talento.futuro.iotapidev.exception.DuplicatedException;
import talento.futuro.iotapidev.exception.NotFoundException;
import talento.futuro.iotapidev.mapper.SensorMapper;
import talento.futuro.iotapidev.model.Location;
import talento.futuro.iotapidev.model.Sensor;
import talento.futuro.iotapidev.repository.LocationRepository;
import talento.futuro.iotapidev.repository.SensorRepository;
import talento.futuro.iotapidev.util.ApiKeyGenerator;

@Service
@RequiredArgsConstructor
@Transactional
public class SensorService {

    private final AuthService authService;
    private final SensorRepository sensorRepository;
    private final LocationService locationService;
    private final SensorMapper sensorMapper;
    private final ApiKeyGenerator apiKeyGenerator;
    private final LocationRepository locationRepository;

    public Page<SensorResponse> getAllSensorForCompany(Pageable pageable) {
        Integer companyId = authService.getCompanyIdFromContext();

        Page<Sensor> sensors = sensorRepository.getAllSensorsForCompany(companyId, pageable);

        return sensors.map(sensorMapper::toSensorResponse);

    }

    public SensorResponse getSensorById(Integer id) {
        Sensor sensor = getSensorForCompany(id);
        return sensorMapper.toSensorResponse(sensor);
    }


    public SensorResponse createSensor(@Valid SensorRequest request) {

        Location location = locationService.getLocationForCompany(request.locationId());

        validateRequest(request);

        Sensor newSensor = Sensor.builder()
                                 .name(request.sensorName())
                                 .category(request.sensorCategory())
                                 .meta(request.sensorMeta())
                                 .apiKey(apiKeyGenerator.generateApiKey())
                                 .location(location)
                                 .build();

        Sensor sensor = sensorRepository.save(newSensor);

        return sensorMapper.toSensorResponse(sensor);
    }

    public SensorResponse updateSensor(Integer id, @Valid SensorRequest request) {

        Location location = locationService.getLocationForCompany(request.locationId());
        
        Sensor sensorForCompany = getSensorForCompany(id);
        
        if (sensorRepository.existsByNameAndIdNot(request.sensorName(), id)) {
        	throw new DuplicatedException("Sensor", request.sensorName());
        }
        
        sensorForCompany.setName(request.sensorName());
        sensorForCompany.setCategory(request.sensorCategory());
        sensorForCompany.setMeta(request.sensorMeta());
        sensorForCompany.setLocation(location);

        Sensor sensor = sensorRepository.save(sensorForCompany);
        return sensorMapper.toSensorResponse(sensor);
    }

    public void deleteSensor(Integer id) {
        Sensor sensor = getSensorForCompany(id);
        sensorRepository.deleteById(sensor.getId());
    }


    private void validateRequest(@Valid SensorRequest request) {
        if (sensorRepository.existsByNameForCompany(request.sensorName(), authService.getCompanyIdFromContext() )) {
            throw new DuplicatedException("Sensor", request.sensorName());
        }
    }


    private Sensor getSensorForCompany(Integer id) {
        Integer companyId = authService.getCompanyIdFromContext();

        return sensorRepository.findSensorByIdForCompany(id, companyId)
                .orElseThrow(() -> new NotFoundException("Sensor",id));
    }

}
