package talento.futuro.iotapidev.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import talento.futuro.iotapidev.dto.Payload;
import talento.futuro.iotapidev.dto.SensorDataResponse;
import talento.futuro.iotapidev.dto.SensorDataSearchCriteria;
import talento.futuro.iotapidev.mapper.SensorDataMapper;
import talento.futuro.iotapidev.model.SensorData;
import talento.futuro.iotapidev.repository.SensorDataSearch;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SensorDataService {

    private final SensorDataSearch sensorDataSearch;
    private final SensorDataMapper sensorDataMapper;
    private final PayloadProcessor payloadProcessor;
    private final AuthService authService;

    public void processPayload(Payload payload) {
        payloadProcessor.extractSensorData(payload);
    }

    public Page<SensorDataResponse> searchData(Long from, Long to, List<Integer> sensorIds, Pageable pageable) {
        Integer companyId = authService.getCompanyIdFromContext();

        SensorDataSearchCriteria criteria = new SensorDataSearchCriteria(from, to, companyId, sensorIds);

        Page<SensorData> search = sensorDataSearch.search(criteria, pageable);
        return search.map(sensorDataMapper::toResponse);

    }

}
