package talento.futuro.iotapidev.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import talento.futuro.iotapidev.constants.ApiBase;
import talento.futuro.iotapidev.constants.ApiPath;
import talento.futuro.iotapidev.dto.SensorRequest;
import talento.futuro.iotapidev.dto.SensorResponse;
import talento.futuro.iotapidev.exception.LocationNotFoundException;
import talento.futuro.iotapidev.exception.SensorNotFoundException;
import talento.futuro.iotapidev.service.AdminSensorService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static talento.futuro.iotapidev.docs.SensorDocumentation.*;
import static talento.futuro.iotapidev.docs.SharedDocumentation.Errors.domainErrorResponseFields;
import static talento.futuro.iotapidev.docs.SharedDocumentation.Pagination.getPageResponseFields;
import static talento.futuro.iotapidev.utils.SensorTestDataFactory.*;

@WithMockUser(roles = "ADMIN")
@WebMvcTest(AdminSensorController.class)
class AdminSensorControllerTest extends BaseRestDocsControllerTest {

    private static final String ADMIN_SENSOR_PATH = ApiBase.V1 + ApiPath.ADMIN + ApiPath.SENSOR;

    @MockitoBean
    private AdminSensorService adminSensorService;

    @Test
    void createSensorAdmin() throws Exception {

        int sensorLocationId = 1;

        SensorRequest request = createDefaultSensorRequest(sensorLocationId, "Sensor 1");
        SensorResponse expectedResponse = createDefaultSensorResponse(1, sensorLocationId, "Sensor 1");

        when(adminSensorService.createSensor(any(SensorRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post(ADMIN_SENSOR_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(expectedResponse.id()))
               .andExpect(jsonPath("$.apiKey").value(expectedResponse.apiKey()))
               .andDo(document("admin/create-sensor",
                       requestFields(sensorAdminRequestFields),
                       responseFields(sensorResponseFields)
               ));
    }

    @Test
    void createSensor_InvalidLocation() throws Exception {

        int locationId = 999;
        SensorRequest request = createDefaultSensorRequest(locationId, "Sensor 1");
        when(adminSensorService.createSensor(any(SensorRequest.class))).thenThrow(new LocationNotFoundException(locationId));

        mockMvc.perform(post(ADMIN_SENSOR_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isNotFound())
               .andDo(document("admin/create-sensor-invalid-location",
                       requestFields(sensorAdminRequestFields),
                       responseFields(domainErrorResponseFields)
               ));
    }

    @Test
    void getAllSensors() throws Exception {

        Pageable requestedPageable = PageRequest.of(0, 20);
        List<SensorResponse> sensorsList = createDefaultSensorResponseList();

        Page<SensorResponse> expectedPage = new PageImpl<>(sensorsList, requestedPageable, sensorsList.size());

        when(adminSensorService.findAllSensors(any(Pageable.class))).thenReturn(expectedPage);

        mockMvc.perform(get(ADMIN_SENSOR_PATH)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content[0].id").value(expectedPage.getContent().get(0).id()))
               .andExpect(jsonPath("$.content[0].locationId").value(expectedPage.getContent().get(0).locationId()))
               .andExpect(jsonPath("$.content[0].name").value(expectedPage.getContent().get(0).name()))
               .andExpect(jsonPath("$.content[0].category").value(expectedPage.getContent().get(0).category()))
               .andExpect(jsonPath("$.content[0].meta").value(expectedPage.getContent().get(0).meta()))
               .andExpect(jsonPath("$.content[0].apiKey").value(expectedPage.getContent().get(0).apiKey()))
               .andExpect(jsonPath("$.content[1].name").value(expectedPage.getContent().get(1).name()))
               .andDo(document("admin/get-all-sensors",
                       responseFields(getPageResponseFields(
                               "Lista paginada de sensores registrados en el sistema.",
                               sensorListResponseFieldsPaginated))));

    }

    @Test
    void getSensorById_Found() throws Exception {
        int sensorId = 1;
        SensorResponse expectedResponse = createDefaultSensorResponse(sensorId, 10, "Sensor Uno");
        when(adminSensorService.findSensorById(sensorId)).thenReturn(expectedResponse);

        mockMvc.perform(get(ADMIN_SENSOR_PATH + "/{id}", sensorId)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(sensorId))
               .andExpect(jsonPath("$.locationId").value(expectedResponse.locationId()))
               .andExpect(jsonPath("$.name").value(expectedResponse.name()))
               .andExpect(jsonPath("$.category").value(expectedResponse.category()))
               .andExpect(jsonPath("$.meta").value(expectedResponse.meta()))
               .andExpect(jsonPath("$.apiKey").value(expectedResponse.apiKey()))
               .andDo(document("admin/get-sensor-by-id",
                       pathParameters(
                               parameterWithName("id").description("El ID numérico del sensor a buscar.")
                       ),
                       responseFields(sensorResponseFields)
               ));
    }

    @Test
    void getSensorById_NotFound() throws Exception {
        int sensorId = 9999;
        when(adminSensorService.findSensorById(sensorId)).thenThrow(new SensorNotFoundException(sensorId));

        mockMvc.perform(get(ADMIN_SENSOR_PATH + "/{id}", sensorId)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andDo(document("admin/get-sensor-by-id-not-found",
                       pathParameters(
                               parameterWithName("id").description("El ID numérico del sensor a buscar.")
                       ),
                       responseFields(domainErrorResponseFields)
               ));
    }

    @Test
    void updateSensor() throws Exception {
        int sensorId = 1;
        String sensorName = "Sensor (actualizado)";
        SensorRequest request = createDefaultSensorRequest(sensorId, sensorName);
        SensorResponse expectedResponse = createDefaultSensorResponse(sensorId, 10, sensorName);

        when(adminSensorService.updateSensor(sensorId, request)).thenReturn(expectedResponse);

        mockMvc.perform(put(ADMIN_SENSOR_PATH + "/{id}", sensorId)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value(sensorName))
               .andDo(document("admin/update-sensor",
                       pathParameters(
                               parameterWithName("id").description("ID numérico del sensor a modificar.")
                       ),
                       requestFields(sensorAdminRequestFields),
                       responseFields(sensorResponseFields)
               ));

    }

    @Test
    void deleteSensor() throws Exception {
        int sensorId = 1;
        doNothing().when(adminSensorService).deleteSensor(eq(sensorId));

        mockMvc.perform(delete(ADMIN_SENSOR_PATH + "/{id}", sensorId))
               .andExpect(status().isNoContent())
               .andDo(document("admin/delete-sensor",
                       pathParameters(
                               parameterWithName("id").description("ID numérico del sensor a eliminar.")
                       )
               ));
    }
}