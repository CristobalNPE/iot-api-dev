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

@RestController
@RequestMapping(ApiBase.V1 + ApiPath.SENSOR_DATA)
@RequiresCompanyApiKey
@RequiredArgsConstructor
public class SensorDataController {

    @PostMapping
    @RequiresCompanyApiKey(required = false)
    @ResponseStatus(HttpStatus.CREATED)
    public SensorResponse createSensorData(@RequestBody @Valid SensorRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
