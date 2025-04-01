package talento.futuro.iotapidev.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import talento.futuro.iotapidev.constants.ApiBase;
import talento.futuro.iotapidev.constants.ApiPath;
import talento.futuro.iotapidev.dto.SensorRequest;
import talento.futuro.iotapidev.dto.SensorResponse;
import talento.futuro.iotapidev.exception.DuplicatedSensorException;
import talento.futuro.iotapidev.exception.LocationNotFoundException;
import talento.futuro.iotapidev.exception.SensorNotFoundException;
import talento.futuro.iotapidev.service.SensorService;
import talento.futuro.iotapidev.utils.WithMockCompany;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static talento.futuro.iotapidev.constants.ApiKeys.COMPANY_API_KEY_HEADER;
import static talento.futuro.iotapidev.docs.SensorDocumentation.*;
import static talento.futuro.iotapidev.docs.SharedDocumentation.Authentication.companyApiKeyHeader;
import static talento.futuro.iotapidev.docs.SharedDocumentation.Authentication.companyApiKeyHeaderInvalid;
import static talento.futuro.iotapidev.docs.SharedDocumentation.Errors.domainErrorResponseFields;
import static talento.futuro.iotapidev.docs.SharedDocumentation.Errors.validationErrorResponseFields;
import static talento.futuro.iotapidev.docs.SharedDocumentation.Pagination.getPageResponseFields;
import static talento.futuro.iotapidev.utils.SensorTestDataFactory.*;
import static talento.futuro.iotapidev.utils.TestUtils.generateApiKeyForTests;

@WebMvcTest(SensorController.class)
class SensorControllerTest extends BaseRestDocsControllerTest {

    private static final String SENSOR_PATH = ApiBase.V1 + ApiPath.SENSOR;

    @MockitoBean
    private SensorService sensorService;

    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void getAllSensors_Success() throws Exception {

        Pageable requestedPageable = PageRequest.of(0, 20);
        List<SensorResponse> sensorsList = createDefaultSensorResponseList();
        Page<SensorResponse> expectedPage = new PageImpl<>(sensorsList, requestedPageable, sensorsList.size());
        when(sensorService.getAllSensorForCompany(any(Pageable.class))).thenReturn(expectedPage);


        mockMvc.perform(get(SENSOR_PATH)
                       .accept(MediaType.APPLICATION_JSON)
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content[0].name").value(expectedPage.getContent().get(0).name()))
               .andExpect(jsonPath("$.content[0].apiKey").value(expectedPage.getContent().get(0).apiKey()))
               .andExpect(jsonPath("$.content[1].apiKey").value(expectedPage.getContent().get(1).apiKey()))
               .andExpect(jsonPath("$.content[2].category").value(expectedPage.getContent().get(2).category()))
               .andDo(document("sensor/get-all",
                       requestHeaders(companyApiKeyHeader),
                       responseFields(getPageResponseFields(
                               "Lista paginada de sensores pertenecientes a la compañía autenticada.",
                               sensorListResponseFieldsPaginated))));
    }

    @Test
    void getAllSensors_InvalidApiKey() throws Exception {
        String invalidApiKey = "invalid-key";
        when(companyRepository.findByApiKey(invalidApiKey)).thenReturn(Optional.empty());

        mockMvc.perform(get(SENSOR_PATH)
                       .accept(MediaType.APPLICATION_JSON)
                       .header(COMPANY_API_KEY_HEADER, invalidApiKey))
               .andExpect(status().isUnauthorized())
               .andDo(document("sensor/get-all-invalid-key",
                       requestHeaders(companyApiKeyHeaderInvalid)));
    }

    @Test
    void getAllSensors_MissingApiKey() throws Exception {
        mockMvc.perform(get(SENSOR_PATH)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isUnauthorized())
               .andDo(document("sensor/get-all-missing-key"));
    }


    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void getSensorById_Success() throws Exception {
        int sensorId = 5;


        SensorResponse expectedResponse = createDefaultSensorResponse(sensorId, 10, "Sensor de Prueba");
        when(sensorService.getSensorById(eq(sensorId))).thenReturn(expectedResponse);

        mockMvc.perform(get(SENSOR_PATH + "/{id}", sensorId)
                       .accept(MediaType.APPLICATION_JSON)
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(sensorId))
               .andExpect(jsonPath("$.name").value(expectedResponse.name()))
               .andDo(document("sensor/get-by-id",
                       requestHeaders(companyApiKeyHeader),
                       pathParameters(parameterWithName("id").description("ID del sensor a obtener.")),
                       responseFields(sensorResponseFields)));
    }

    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void getSensorById_NotFoundForCompany() throws Exception {
        int sensorId = 99;

        when(sensorService.getSensorById(eq(sensorId))).thenThrow(new SensorNotFoundException(sensorId));

        mockMvc.perform(get(SENSOR_PATH + "/{id}", sensorId)
                       .accept(MediaType.APPLICATION_JSON)
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY))
               .andExpect(status().isNotFound())
               .andDo(document("sensor/get-by-id-not-found",
                       requestHeaders(companyApiKeyHeader),
                       pathParameters(parameterWithName("id").description("ID del sensor no encontrado para esta compañía.")),
                       responseFields(domainErrorResponseFields)));
    }


    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void createSensor_Success() throws Exception {
        int locationId = 2;


        SensorRequest request = createDefaultSensorRequest(locationId, "Nuevo Sensor Temp");
        SensorResponse expectedResponse = createSensorResponseFromRequest(15, request);

        when(sensorService.createSensor(any(SensorRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post(SENSOR_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request))
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(expectedResponse.id()))
               .andExpect(jsonPath("$.name").value(request.sensorName()))
               .andExpect(jsonPath("$.apiKey").isNotEmpty())
               .andDo(document("sensor/create",
                       requestHeaders(companyApiKeyHeader),
                       requestFields(sensorAdminRequestFields),
                       responseFields(sensorResponseFields)));
    }

    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void createSensor_ValidationError() throws Exception {
        int locationId = 2;


        // invalid, blank name
        SensorRequest request = new SensorRequest(locationId, " ", "Category", "Meta");
        // validation goes before service call

        mockMvc.perform(post(SENSOR_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request))
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY)) // For docs
               .andExpect(status().isBadRequest())
               .andDo(document("sensor/create-validation-error",
                       requestHeaders(companyApiKeyHeader),
                       requestFields(sensorAdminRequestFields),
                       responseFields(validationErrorResponseFields("sensorName", "SensorName can't be empty")))); // Example
    }

    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void createSensor_DuplicateName() throws Exception {
        int locationId = 2;


        SensorRequest request = createDefaultSensorRequest(locationId, "Sensor Existente");
        when(sensorService.createSensor(any(SensorRequest.class))).thenThrow(new DuplicatedSensorException(request.sensorName()));

        mockMvc.perform(post(SENSOR_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request))
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY))
               .andExpect(status().isConflict())
               .andDo(document("sensor/create-duplicate-error",
                       requestHeaders(companyApiKeyHeader),
                       requestFields(sensorAdminRequestFields),
                       responseFields(domainErrorResponseFields)));
    }


    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void createSensor_LocationNotFoundForCompany() throws Exception {
        int invalidLocationId = 999;


        SensorRequest request = createDefaultSensorRequest(invalidLocationId, "Sensor For Invalid Loc");

        when(sensorService.createSensor(any(SensorRequest.class))).thenThrow(new LocationNotFoundException(invalidLocationId));

        mockMvc.perform(post(SENSOR_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request))
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY))
               .andExpect(status().isNotFound())
               .andDo(document("sensor/create-location-not-found",
                       requestHeaders(companyApiKeyHeader),
                       requestFields(sensorAdminRequestFields),
                       responseFields(domainErrorResponseFields)));
    }


    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void updateSensor_Success() throws Exception {
        int sensorId = 8;
        int newLocationId = 3;


        SensorRequest request = new SensorRequest(newLocationId, "Sensor Prueba (actualizado)",
                "Categoría actualizada",
                "Metadatos actualizados");
        SensorResponse expectedResponse = new SensorResponse(sensorId, newLocationId, request.sensorName(), request.sensorCategory(), request.sensorMeta(), generateApiKeyForTests());

        when(sensorService.updateSensor(eq(sensorId), any(SensorRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(put(SENSOR_PATH + "/{id}", sensorId)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request))
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(sensorId))
               .andExpect(jsonPath("$.name").value(request.sensorName()))
               .andExpect(jsonPath("$.locationId").value(newLocationId))
               .andDo(document("sensor/update",
                       requestHeaders(companyApiKeyHeader),
                       pathParameters(parameterWithName("id").description("ID del sensor a actualizar.")),
                       requestFields(sensorAdminRequestFields),
                       responseFields(sensorResponseFields)));
    }

    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void updateSensor_NotFoundForCompany() throws Exception {
        int sensorId = 99;
        int locationId = 3;


        SensorRequest request = createDefaultSensorRequest(locationId, "Sensor Prueba (intento de modif.)");
        when(sensorService.updateSensor(eq(sensorId), any(SensorRequest.class))).thenThrow(new SensorNotFoundException(sensorId));

        mockMvc.perform(put(SENSOR_PATH + "/{id}", sensorId)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request))
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY))
               .andExpect(status().isNotFound())
               .andDo(document("sensor/update-sensor-not-found",
                       requestHeaders(companyApiKeyHeader),
                       pathParameters(parameterWithName("id").description("ID del sensor no encontrado para esta compañía.")),
                       requestFields(sensorAdminRequestFields),
                       responseFields(domainErrorResponseFields)));
    }

    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void updateSensor_NewLocationNotFoundForCompany() throws Exception {
        int sensorId = 8;
        int invalidNewLocationId = 999;


        SensorRequest request = createDefaultSensorRequest(invalidNewLocationId, "Update To Invalid Loc");

        when(sensorService.updateSensor(eq(sensorId), any(SensorRequest.class))).thenThrow(new LocationNotFoundException(invalidNewLocationId));

        mockMvc.perform(put(SENSOR_PATH + "/{id}", sensorId)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request))
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY))
               .andExpect(status().isNotFound())
               .andDo(document("sensor/update-new-location-not-found",
                       requestHeaders(companyApiKeyHeader),
                       pathParameters(parameterWithName("id").description("ID del sensor que se intenta actualizar.")),
                       requestFields(sensorAdminRequestFields),
                       responseFields(domainErrorResponseFields)));
    }


    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void deleteSensor_Success() throws Exception {
        int sensorId = 12;

        doNothing().when(sensorService).deleteSensor(eq(sensorId));

        mockMvc.perform(delete(SENSOR_PATH + "/{id}", sensorId)
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY))
               .andExpect(status().isNoContent())
               .andDo(document("sensor/delete",
                       requestHeaders(companyApiKeyHeader),
                       pathParameters(parameterWithName("id").description("ID del sensor a eliminar."))));
    }

    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void deleteSensor_NotFoundForCompany() throws Exception {
        int sensorId = 99;

        doThrow(new SensorNotFoundException(sensorId)).when(sensorService).deleteSensor(eq(sensorId));

        mockMvc.perform(delete(SENSOR_PATH + "/{id}", sensorId)
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY))
               .andExpect(status().isNotFound())
               .andDo(document("sensor/delete-not-found",
                       requestHeaders(companyApiKeyHeader),
                       pathParameters(parameterWithName("id").description("ID del sensor no encontrado para esta compañía.")),
                       responseFields(domainErrorResponseFields)));
    }


    @Test
    void updateSensor_InvalidApiKey() throws Exception {
        int sensorId = 8;
        int locationId = 3;
        String invalidApiKey = "invalid-key";
        when(companyRepository.findByApiKey(invalidApiKey)).thenReturn(Optional.empty());

        SensorRequest request = createDefaultSensorRequest(locationId, "Update Attempt");

        mockMvc.perform(put(SENSOR_PATH + "/{id}", sensorId)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request))
                       .header(COMPANY_API_KEY_HEADER, invalidApiKey))
               .andExpect(status().isUnauthorized())
               .andDo(document("sensor/update-invalid-key",
                       requestHeaders(companyApiKeyHeaderInvalid),
                       pathParameters(parameterWithName("id").description("ID del sensor a actualizar.")),
                       requestFields(sensorAdminRequestFields)
               ));
    }

    @Test
    void deleteSensor_MissingApiKey() throws Exception {
        int sensorId = 12;
        mockMvc.perform(delete(SENSOR_PATH + "/{id}", sensorId))
               .andExpect(status().isUnauthorized())
               .andDo(document("sensor/delete-missing-key",
                       pathParameters(parameterWithName("id").description("ID del sensor a eliminar."))
               ));
    }
}