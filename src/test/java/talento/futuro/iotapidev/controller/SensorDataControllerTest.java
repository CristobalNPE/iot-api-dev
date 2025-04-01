package talento.futuro.iotapidev.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import talento.futuro.iotapidev.constants.ApiBase;
import talento.futuro.iotapidev.constants.ApiPath;
import talento.futuro.iotapidev.dto.Payload;
import talento.futuro.iotapidev.dto.SensorDataResponse;
import talento.futuro.iotapidev.exception.InvalidSensorApiKeyException;
import talento.futuro.iotapidev.exception.SensorNotFoundException;
import talento.futuro.iotapidev.service.SensorDataService;
import talento.futuro.iotapidev.utils.WithMockCompany;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static talento.futuro.iotapidev.constants.ApiKeys.COMPANY_API_KEY_HEADER;
import static talento.futuro.iotapidev.constants.ApiKeys.COMPANY_API_KEY_PARAM;
import static talento.futuro.iotapidev.docs.SensorDataDocumentation.payloadRequestFields;
import static talento.futuro.iotapidev.docs.SensorDataDocumentation.sensorDataListResponseFieldsPaginated;
import static talento.futuro.iotapidev.docs.SharedDocumentation.Authentication.companyApiKeyHeader;
import static talento.futuro.iotapidev.docs.SharedDocumentation.Authentication.companyApiKeyHeaderInvalid;
import static talento.futuro.iotapidev.docs.SharedDocumentation.Errors.domainErrorResponseFields;
import static talento.futuro.iotapidev.docs.SharedDocumentation.Pagination.getPageResponseFields;
import static talento.futuro.iotapidev.docs.SharedDocumentation.Pagination.getQueryParametersWithPagination;
import static talento.futuro.iotapidev.utils.SensorDataTestDataFactory.*;
import static talento.futuro.iotapidev.utils.TestUtils.generateApiKeyForTests;

@WebMvcTest(SensorDataController.class)
class SensorDataControllerTest extends BaseRestDocsControllerTest {

    private static final String SENSOR_DATA_PATH = ApiBase.V1 + ApiPath.SENSOR_DATA;

    @MockitoBean
    private SensorDataService sensorDataService;


    @Test
    void registerPayload_Success() throws Exception {
        String validSensorApiKey = generateApiKeyForTests();

        Payload payload = createValidSuccessPayload(validSensorApiKey);

        doNothing().when(sensorDataService).processPayload(any(Payload.class)); //void

        mockMvc.perform(post(SENSOR_DATA_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(payload)))
               .andDo(print())
               .andExpect(status().isCreated())
               .andDo(document("sensor-data/register-payload",
                       requestFields(payloadRequestFields)
               ));
    }

    //todo: need exception handling
    @Test
    @Disabled // todo: enable when ready
    void registerPayload_InvalidSensorApiKey() throws Exception {
        String invalidSensorApiKey = "invalid-sensor-key";

        Payload payload = createValuePayload(invalidSensorApiKey);

        doThrow(new InvalidSensorApiKeyException(invalidSensorApiKey))
                .when(sensorDataService).processPayload(any(Payload.class));

        mockMvc.perform(post(SENSOR_DATA_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(payload)))
               .andExpect(status().isBadRequest())
               .andDo(document("sensor-data/register-payload-invalid-key",
                       requestFields(payloadRequestFields),
                       responseFields(domainErrorResponseFields)
               ));
    }

    @Test
    @Disabled // todo: enable when ready
    void registerPayload_InvalidJsonStructure() throws Exception {
        String invalidJson = """
                {
                    "api_key": "api-key-123",
                    "json_data": "estructura-json-invalida"
                }
                """;

        mockMvc.perform(post(SENSOR_DATA_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(invalidJson))
               .andExpect(status().isBadRequest())
               .andDo(document("sensor-data/register-payload-invalid-json"));
        //todo: if we get this in the golbalExceptionHandler, document it with responseFields
    }

    private final ParameterDescriptor[] searchSpecificParams = {
            parameterWithName("from").description("Timestamp EPOCH (inicio del rango). Opcional.").optional(),
            parameterWithName("to").description("Timestamp EPOCH (fin del rango). Opcional.").optional(),
            parameterWithName("sensor_id").description("Lista de IDs de sensores a consultar (repetir parámetro o separar con comas). Opcional.").optional(),
            parameterWithName(COMPANY_API_KEY_PARAM).description("API Key de la compañía (alternativa a la cabecera X-Company-Api-Key).").optional()

    };


    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void searchData_SuccessWithParams() throws Exception {


        long fromTs = 1700000000L;
        long toTs = 1710000000L;
        List<Integer> sensorIds = List.of(2, 5, 10);
        Pageable pageable = PageRequest.of(0, 10);

        List<SensorDataResponse> responseData = createSensorDataResponseList(sensorIds, fromTs, 1);
        Page<SensorDataResponse> expectedPage = new PageImpl<>(responseData, pageable, 50);

        when(sensorDataService.searchData(eq(fromTs), eq(toTs), eq(sensorIds), any(Pageable.class)))
                .thenReturn(expectedPage);

        mockMvc.perform(get(SENSOR_DATA_PATH)
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY)
                       .param("from", String.valueOf(fromTs))
                       .param("to", String.valueOf(toTs))
                       .param("sensor_id", "2", "5", "10")
                       .param("page", "0")
                       .param("size", "10")
                       .param(COMPANY_API_KEY_PARAM, VALID_COMPANY_API_KEY)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", hasSize(responseData.size())))
               .andExpect(jsonPath("$.page.totalElements").value(expectedPage.getTotalElements()))
               .andDo(document("sensor-data/search",
                       requestHeaders(companyApiKeyHeader),
                       queryParameters(getQueryParametersWithPagination(searchSpecificParams)),
                       responseFields(getPageResponseFields(
                               "Datos paginados de sensores.", sensorDataListResponseFieldsPaginated) // Define these
                       )));
    }

    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void searchData_SuccessNoParams() throws Exception {

        long fromTs = 1700000000L;
        long toTs = 1710000000L;
        List<Integer> sensorIds = List.of(2, 5, 10);
        Pageable pageable = PageRequest.of(0, 10);

        List<SensorDataResponse> responseData = createSensorDataResponseList(sensorIds, fromTs, 1);
        Page<SensorDataResponse> expectedPage = new PageImpl<>(responseData, pageable, 50);

        when(sensorDataService.searchData(isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(expectedPage);

        mockMvc.perform(get(SENSOR_DATA_PATH)
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", hasSize(responseData.size())))
               .andDo(document("sensor-data/search-no-params",
                       requestHeaders(companyApiKeyHeader),
                       queryParameters(getQueryParametersWithPagination(searchSpecificParams)),
                       responseFields(getPageResponseFields(
                               "Datos paginados de sensores.", sensorDataListResponseFieldsPaginated)
                       )));
    }


    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void deleteAllSensorData_Success() throws Exception {
        int sensorIdToDelete = 15;

        doNothing().when(sensorDataService).deleteAllSensorData(eq(sensorIdToDelete));

        mockMvc.perform(delete(SENSOR_DATA_PATH + "/{sensorId}", sensorIdToDelete)
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY))
               .andExpect(status().isNoContent())
               .andDo(document("sensor-data/delete-all-data",
                       requestHeaders(companyApiKeyHeader),
                       pathParameters(parameterWithName("sensorId").description("ID del sensor cuyos datos se eliminarán."))
               ));
    }

    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void deleteAllSensorData_SensorNotFoundForCompany() throws Exception {
        int sensorIdToDelete = 99;


        doThrow(new SensorNotFoundException(sensorIdToDelete))
                .when(sensorDataService).deleteAllSensorData(eq(sensorIdToDelete));

        mockMvc.perform(delete(SENSOR_DATA_PATH + "/{sensorId}", sensorIdToDelete)
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY))
               .andExpect(status().isNotFound())
               .andDo(document("sensor-data/delete-all-data-not-found",
                       requestHeaders(companyApiKeyHeader),
                       pathParameters(parameterWithName("sensorId").description("ID del sensor no encontrado para la compañía.")),
                       responseFields(domainErrorResponseFields)
               ));

    }

    @Test
    void searchData_InvalidApiKey() throws Exception {
        String invalidApiKey = "invalid-company-key";
        when(companyRepository.findByApiKey(invalidApiKey)).thenReturn(Optional.empty());

        mockMvc.perform(get(SENSOR_DATA_PATH)
                       .header(COMPANY_API_KEY_HEADER, invalidApiKey)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isUnauthorized())
               .andDo(document("sensor-data/search-invalid-key",
                       requestHeaders(companyApiKeyHeaderInvalid)));
    }

    @Test
    void deleteAllSensorData_MissingApiKey() throws Exception {
        int sensorIdToDelete = 15;
        mockMvc.perform(delete(SENSOR_DATA_PATH + "/{sensorId}", sensorIdToDelete))
               .andExpect(status().isUnauthorized())
               .andDo(document("sensor-data/delete-all-data-missing-key",
                       pathParameters(parameterWithName("sensorId").description("ID del sensor."))
               ));
    }

}