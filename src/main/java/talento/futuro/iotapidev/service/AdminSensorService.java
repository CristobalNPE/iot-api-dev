package talento.futuro.iotapidev.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import talento.futuro.iotapidev.dto.SensorRequest;
import talento.futuro.iotapidev.dto.SensorResponse;
import talento.futuro.iotapidev.exception.DuplicatedSensorException;
import talento.futuro.iotapidev.exception.LocationNotFoundException;
import talento.futuro.iotapidev.exception.SensorNotFoundException;
import talento.futuro.iotapidev.mapper.SensorMapper;
import talento.futuro.iotapidev.model.Location;
import talento.futuro.iotapidev.model.Sensor;
import talento.futuro.iotapidev.repository.LocationRepository;
import talento.futuro.iotapidev.repository.SensorRepository;
import talento.futuro.iotapidev.util.ApiKeyGenerator;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminSensorService {

    private final SensorRepository sensorRepository;
    private final SensorMapper sensorMapper;
    private final ApiKeyGenerator apiKeyGenerator;
    private final LocationRepository locationRepository;


    private void validateRequest(@Valid SensorRequest request) {
        if (sensorRepository.existsByName(request.sensorName())) {
            throw new DuplicatedSensorException(request.sensorName());
        }
    }

    public Page<SensorResponse> findAllSensors(Pageable pageable) {
        Page<Sensor> sensors = sensorRepository.findAll(pageable);

        return sensors.map(sensorMapper::toSensorResponse);
    }

    public SensorResponse findSensorById(Integer sensorId) {
        return sensorRepository.findById(sensorId)
                .map(sensorMapper::toSensorResponse)
                .orElseThrow(() -> new SensorNotFoundException(sensorId));
    }

    public SensorResponse createSensor(@Valid SensorRequest request) {
        Location location = locationRepository.findById(request.locationId())
                .orElseThrow(() -> new LocationNotFoundException(request.locationId()));

        validateRequest(request);

        Sensor newSensor = Sensor
                .builder()
                .location(location)
                .name(request.sensorName())
                .category(request.sensorCategory())
                .meta(request.sensorMeta())
                .apiKey(apiKeyGenerator.generateApiKey())
                .build();

        return sensorMapper.toSensorResponse(sensorRepository.save(newSensor));
    }

    public SensorResponse updateSensor(Integer sensorId, @Valid SensorRequest request) {

        Location location = locationRepository.findById(request.locationId())
                .orElseThrow(() -> new LocationNotFoundException(request.locationId()));

        Sensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new SensorNotFoundException(sensorId));

        sensor.setLocation(location);
        sensor.setName(request.sensorName());
        sensor.setMeta(request.sensorMeta());
        sensor.setCategory(request.sensorCategory());

        return sensorMapper.toSensorResponse(sensorRepository.save(sensor));
    }

    public void deleteSensor(Integer sensorId) {
        if (!sensorRepository.existsById(sensorId)) {
            throw new SensorNotFoundException(sensorId);
        }
        sensorRepository.deleteById(sensorId);
    }
}

