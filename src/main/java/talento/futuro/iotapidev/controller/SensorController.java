package talento.futuro.iotapidev.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import talento.futuro.iotapidev.annotation.RequiresCompanyApiKey;
import talento.futuro.iotapidev.constants.ApiBase;
import talento.futuro.iotapidev.constants.ApiPath;
import talento.futuro.iotapidev.dto.SensorRequest;
import talento.futuro.iotapidev.dto.SensorResponse;
import talento.futuro.iotapidev.service.SensorService;

import java.util.List;

@RestController
@RequestMapping(ApiBase.V1 + ApiPath.SENSOR)
@RequiresCompanyApiKey
@RequiredArgsConstructor
public class SensorController {

    private final SensorService sensorService;

    @GetMapping
    public List<SensorResponse> getAllSensors() {
        return sensorService.getAllSensorForCompany();
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
