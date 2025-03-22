package talento.futuro.iotapidev.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import talento.futuro.iotapidev.constants.ApiBase;
import talento.futuro.iotapidev.constants.ApiPath;

@RestController
@RequestMapping(ApiBase.V1 + ApiPath.SENSOR_DATA)
@RequiredArgsConstructor
public class SensorDataController {
}
