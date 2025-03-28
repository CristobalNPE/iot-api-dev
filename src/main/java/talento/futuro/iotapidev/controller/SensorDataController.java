package talento.futuro.iotapidev.controller;

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

import static talento.futuro.iotapidev.constants.ApiKeys.COMPANY_API_KEY_PARAM;

@Slf4j
@RestController
@RequestMapping(ApiBase.V1 + ApiPath.SENSOR_DATA)
@RequiredArgsConstructor
public class SensorDataController {

    private final SensorDataService sensorDataService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void registerPayload(@RequestBody Payload payload) {
        log.info("\n🟢 Payload recibido\n {}", payload);
        sensorDataService.processPayload(payload);
    }


    @GetMapping
    public Page<SensorDataResponse> searchData(@RequestParam(required = false, value = "from") Long from,
                                               @RequestParam(required = false, value = "to") Long to,
                                               @RequestParam(required = false, value = "sensor_id") List<Integer> sensorIds,
                                               @RequestParam(required = false, value = COMPANY_API_KEY_PARAM) String companyApiKey,
                                               Pageable pageable
    ) {

        log.info("From: {} ", from);
        log.info("To: {} ", to);
        log.info("SensorIds: {} ", sensorIds);
        log.info("CompanyApiKey: {} ", companyApiKey);

        return sensorDataService.searchData(from,to,sensorIds, pageable);

    }
}
