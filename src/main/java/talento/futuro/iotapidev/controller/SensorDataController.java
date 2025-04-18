package talento.futuro.iotapidev.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import talento.futuro.iotapidev.constants.ApiBase;
import talento.futuro.iotapidev.constants.ApiPath;
import talento.futuro.iotapidev.dto.Payload;
import talento.futuro.iotapidev.dto.SensorDataResponse;
import talento.futuro.iotapidev.service.SensorDataService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(ApiBase.V1 + ApiPath.SENSOR_DATA)
@RequiredArgsConstructor
public class SensorDataController {

    private final SensorDataService sensorDataService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void registerPayload(@RequestBody @Valid Payload payload) {
        log.info("\n🟢 Payload recibido\n {}", payload);
        sensorDataService.processPayload(payload);
    }


    @GetMapping
    public Page<SensorDataResponse> searchData(@RequestParam(required = false, value = "from") Long from,
                                               @RequestParam(required = false, value = "to") Long to,
                                               @RequestParam(required = false, value = "sensor_id") List<Integer> sensorIds,
                                               Pageable pageable
    ) {

        log.info("From: {} ", from);
        log.info("To: {} ", to);
        log.info("SensorIds: {} ", sensorIds);

        return sensorDataService.searchData(from, to, sensorIds, pageable);

    }

    @DeleteMapping("/{sensorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllSensorData(@PathVariable(name = "sensorId") Integer sensorId) {
        sensorDataService.deleteAllSensorData(sensorId);
    }
}
