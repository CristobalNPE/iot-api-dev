package talento.futuro.iotapidev.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import talento.futuro.iotapidev.dto.SensorRequest;
import talento.futuro.iotapidev.dto.SensorResponse;
import talento.futuro.iotapidev.service.SensorService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sensors")
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
    public SensorResponse createSensor(@RequestBody @Valid SensorRequest request) {
        return sensorService.createSensor(request);
    }

    @PutMapping("/{id}")
    public SensorResponse updateSensor(@PathVariable(value = "id") Integer id,
                                       @RequestBody @Valid SensorRequest request) {
        return sensorService.updateSensor(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteSensor(@PathVariable(value = "id") Integer id) {
        sensorService.deleteSensor(id);
    }


}
