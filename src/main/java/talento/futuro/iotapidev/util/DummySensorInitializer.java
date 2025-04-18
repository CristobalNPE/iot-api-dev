package talento.futuro.iotapidev.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import talento.futuro.iotapidev.model.Company;
import talento.futuro.iotapidev.model.Location;
import talento.futuro.iotapidev.model.Sensor;
import talento.futuro.iotapidev.repository.CompanyRepository;
import talento.futuro.iotapidev.repository.LocationRepository;
import talento.futuro.iotapidev.repository.SensorRepository;

import java.util.UUID;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class DummySensorInitializer {

    private final CompanyRepository companyRepository;
    private final LocationRepository locationRepository;
    private final SensorRepository sensorRepository;

    @Value("${app.dummy.sensor.api-key}")
    private String dummySensorApiKey;

    @Value("${app.dummy.sensor.prefix:DUMMY}")
    private String prefix;

    @PostConstruct
    @Transactional
    public void initializeDummyData() {
        try {
            log.info("✨✨ Initializing dummy data...");

            Company dummyCompany = createAndSaveCompany();
            Location dummyLocation = createAndSaveLocation(dummyCompany);
            Sensor dummySensor = createAndSaveSensor(dummyLocation);

            log.info("🟢 Successfully initialized dummy data with sensor ID: {}", dummySensor.getId());
        } catch (Exception e) {
            log.error("🔴 Failed to initialize dummy data: {}", e.getMessage(), e);
        }
    }

    private Company createAndSaveCompany() {
        Company company = Company.builder()
                                 .name(prefix + " Company")
                                 .apiKey(generateDummyApiKey())
                                 .build();

        log.debug("Creating company: {}", company.getName());
        return companyRepository.save(company);
    }

    private Location createAndSaveLocation(Company company) {
        Location location = Location.builder()
                                    .company(company)
                                    .name(prefix + " Location")
                                    .country("Chile")
                                    .city("Santiago")
                                    .meta(prefix + " Location Meta Information")
                                    .build();

        log.debug("Creating location: {}", location.getName());
        return locationRepository.save(location);
    }

    private Sensor createAndSaveSensor(Location location) {
        Sensor sensor = Sensor.builder()
                              .name(prefix + " Sensor")
                              .location(location)
                              .category("Test Category")
                              .meta(prefix + " Sensor Meta Information")
                              .apiKey(dummySensorApiKey)
                              .build();

        log.debug("Creating sensor: {}", sensor.getName());
        return sensorRepository.save(sensor);
    }

    private String generateDummyApiKey() {
        return prefix + "_API_KEY_" + UUID.randomUUID().toString().replace("-", "");
    }

}
