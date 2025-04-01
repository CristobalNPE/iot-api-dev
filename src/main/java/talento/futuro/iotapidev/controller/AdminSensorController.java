package talento.futuro.iotapidev.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import talento.futuro.iotapidev.constants.ApiBase;
import talento.futuro.iotapidev.constants.ApiPath;
import talento.futuro.iotapidev.dto.SensorRequest;
import talento.futuro.iotapidev.dto.SensorResponse;
import talento.futuro.iotapidev.service.AdminSensorService;

@RestController
@RequestMapping(ApiBase.V1 + ApiPath.ADMIN + ApiPath.SENSOR)
@RequiredArgsConstructor
public class AdminSensorController {
    private final AdminSensorService sensorService;

    @GetMapping
    public Page<SensorResponse> getAllSensors(Pageable pageable) {
        return sensorService.findAllSensors(pageable);
    }

    @GetMapping("/{sensorId}")
    public SensorResponse getSensorById(@PathVariable(name = "sensorId") Integer sensorId) {
        return sensorService.findSensorById(sensorId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SensorResponse createSensor(@RequestBody @Valid SensorRequest request) {
        return sensorService.createSensor(request);
    }

    @PutMapping("/{sensorId}")
    public SensorResponse updateSensor(@RequestBody @Valid SensorRequest request, @PathVariable(name = "sensorId") Integer sensorId) {
        return sensorService.updateSensor(sensorId, request);
    }

    @DeleteMapping("/{sensorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSensor(@PathVariable(name = "sensorId") Integer sensorId) {
        sensorService.deleteSensor(sensorId);
    }
}
