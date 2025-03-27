package talento.futuro.iotapidev.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import talento.futuro.iotapidev.constants.ApiBase;
import talento.futuro.iotapidev.constants.ApiPath;
import talento.futuro.iotapidev.dto.SensorRequest;
import talento.futuro.iotapidev.dto.SensorResponse;
import talento.futuro.iotapidev.service.SensorService;

import java.util.List;

@RestController
@RequestMapping(ApiBase.V1 + ApiPath.ADMIN + ApiPath.SENSOR)
@RequiredArgsConstructor
public class AdminSensorController {
    private final SensorService sensorService;

    @GetMapping
    public List<SensorResponse> getAllSensors() {
        return sensorService.adminFindAllSensors();
    }

    @GetMapping("/{sensorId}")
    public SensorResponse getSensorById(@PathVariable(name = "sensorId") Integer sensorId) {
        return sensorService.adminFindSensorById(sensorId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SensorResponse createSensor(@RequestBody @Valid SensorRequest request) {
        return sensorService.adminCreateLocation(request);
    }

    @PutMapping("/{sensorId}")
    public SensorResponse updateSensor(@RequestBody @Valid SensorRequest request, @PathVariable(name = "sensorId") Integer sensorId) {
        return sensorService.adminUpdateSensor(sensorId, request);
    }

    @DeleteMapping("/{sensorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSensor(@PathVariable(name = "sensorId") Integer sensorId) {
        sensorService.adminDeleteLocation(sensorId);
    }
}
