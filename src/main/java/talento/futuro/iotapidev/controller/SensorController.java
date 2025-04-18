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
import talento.futuro.iotapidev.service.SensorService;

@RestController
@RequestMapping(ApiBase.V1 + ApiPath.SENSOR)
@RequiredArgsConstructor
public class SensorController {

    private final SensorService sensorService;

    @GetMapping
    public Page<SensorResponse> getAllSensors(Pageable pageable) {
        return sensorService.getAllSensorForCompany(pageable);
    }

    @GetMapping("/{id}")
    public SensorResponse getSensorById(@PathVariable(value = "id") Integer id) {
        return sensorService.getSensorById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SensorResponse createSensor(@RequestBody @Valid SensorRequest request) {
        return sensorService.createSensor(request);
    }

    @PutMapping("/{id}")
    public SensorResponse updateSensor(@PathVariable(value = "id") Integer id,
                                       @RequestBody @Valid SensorRequest request) {
        return sensorService.updateSensor(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSensor(@PathVariable(value = "id") Integer id) {
        sensorService.deleteSensor(id);
    }

}
