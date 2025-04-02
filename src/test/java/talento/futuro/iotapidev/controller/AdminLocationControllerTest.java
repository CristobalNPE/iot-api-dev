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
import talento.futuro.iotapidev.dto.LocationAdminRequest;
import talento.futuro.iotapidev.dto.LocationResponse;
import talento.futuro.iotapidev.exception.CompanyNotFoundException;
import talento.futuro.iotapidev.exception.LocationNotFoundException;
import talento.futuro.iotapidev.service.AdminLocationService;

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
import static talento.futuro.iotapidev.docs.LocationDocumentation.*;
import static talento.futuro.iotapidev.docs.SharedDocumentation.Errors.domainErrorResponseFields;
import static talento.futuro.iotapidev.docs.SharedDocumentation.Errors.validationErrorResponseFields;
import static talento.futuro.iotapidev.docs.SharedDocumentation.Pagination.getPageResponseFields;
import static talento.futuro.iotapidev.utils.LocationTestDataFactory.*;

@WebMvcTest(AdminLocationController.class)
@WithMockUser(roles = "ADMIN")
class AdminLocationControllerTest extends BaseRestDocsControllerTest {

    @MockitoBean
    private AdminLocationService adminLocationService;

    private static final String ADMIN_LOCATION_PATH = ApiBase.V1 + ApiPath.ADMIN + ApiPath.LOCATION;

    @Test
    void createLocationAdmin() throws Exception {

        LocationAdminRequest request = createDefaultLocationAdminRequest(4, "Ubicación de Prueba");
        LocationResponse expectedResponse = createDefaultLocationResponse(1, "Ubicación de Prueba");

        when(adminLocationService.adminCreateLocation(any(LocationAdminRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post(ADMIN_LOCATION_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(expectedResponse.id()))
               .andDo(document("admin/create-location",
                       requestFields(locationAdminRequestFields),
                       responseFields(locationResponseFields)
               ));
    }

    @Test
    void createLocation_ValidationError() throws Exception {

        LocationAdminRequest invalidRequest = createDefaultLocationAdminRequest(1, "");// empty name

        mockMvc.perform(post(ADMIN_LOCATION_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(invalidRequest)))
               .andExpect(status().isBadRequest())
               .andDo(document("admin/create-location-validation-error",
                       requestFields(locationAdminRequestFields),
                       responseFields(validationErrorResponseFields("name", "Name can't be empty"))
               ));
    }

    @Test
    void createLocationAdmin_NotFoundCompanyId() throws Exception {

        LocationAdminRequest request = createDefaultLocationAdminRequest(999, "Ubicación de Prueba");
        when(adminLocationService.adminCreateLocation(any(LocationAdminRequest.class))).thenThrow(new CompanyNotFoundException(999));

        mockMvc.perform(post(ADMIN_LOCATION_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isNotFound())
               .andDo(document("admin/create-location-invalid-company-id",
                       requestFields(locationAdminRequestFields),
                       responseFields(domainErrorResponseFields)
               ));
    }

    @Test
    void getAllLocations() throws Exception {

        Pageable requestedPageable = PageRequest.of(0, 20);
        List<LocationResponse> locationsList = createDefaultLocationResponseList();
        Page<LocationResponse> expectedPage = new PageImpl<>(locationsList, requestedPageable, locationsList.size());

        when(adminLocationService.adminFindAllLocations(any(Pageable.class))).thenReturn(expectedPage);

        mockMvc.perform(get(ADMIN_LOCATION_PATH)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content[0].id").value(expectedPage.getContent().get(0).id()))
               .andExpect(jsonPath("$.content[0].name").value(expectedPage.getContent().get(0).name()))
               .andExpect(jsonPath("$.content[0].country").value(expectedPage.getContent().get(0).country()))
               .andExpect(jsonPath("$.content[0].city").value(expectedPage.getContent().get(0).city()))
               .andExpect(jsonPath("$.content[0].meta").value(expectedPage.getContent().get(0).meta()))
               .andExpect(jsonPath("$.content[1].name").value(expectedPage.getContent().get(1).name()))
               .andExpect(jsonPath("$.content[2].name").value(expectedPage.getContent().get(2).name()))
               .andDo(document("admin/get-all-locations",
                       responseFields(getPageResponseFields(
                               "Lista paginada de ubicaciones registradas en el sistema.", locationListResponseFieldsPaginated)
                       ))
               );
    }

    @Test
    void getLocationById_Found() throws Exception {

        int locationId = 1;
        LocationResponse expectedResponse = createDefaultLocationResponse(locationId, "Ubicación de Prueba");

        when(adminLocationService.adminFindLocationById(locationId)).thenReturn(expectedResponse);

        mockMvc.perform(get(ADMIN_LOCATION_PATH + "/{id}", locationId)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(locationId))
               .andExpect(jsonPath("$.name").value(expectedResponse.name()))
               .andExpect(jsonPath("$.country").value(expectedResponse.country()))
               .andExpect(jsonPath("$.city").value(expectedResponse.city()))
               .andExpect(jsonPath("$.meta").value(expectedResponse.meta()))
               .andDo(document("admin/get-location-by-id",
                       pathParameters(
                               parameterWithName("id").description("El ID numérico de la ubicación a buscar.")
                       ),
                       responseFields(locationResponseFields)
               ));
    }

    @Test
    void getLocationById_NotFound() throws Exception {

        int locationId = 9999;
        when(adminLocationService.adminFindLocationById(locationId)).thenThrow(new LocationNotFoundException(locationId));

        mockMvc.perform(get(ADMIN_LOCATION_PATH + "/{id}", locationId)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andDo(document("admin/get-location-by-id-not-found",
                       pathParameters(
                               parameterWithName("id").description("El ID de la ubicación que no fue encontrada.")
                       ),
                       responseFields(domainErrorResponseFields)
               ));
    }

    @Test
    void updateLocation() throws Exception {

        int locationId = 1;
        String locationName = "Ubicación de Prueba (actualizada)";
        LocationAdminRequest request = createDefaultLocationAdminRequest(1, locationName);
        LocationResponse expectedResponse = createDefaultLocationResponse(locationId, locationName);

        when(adminLocationService.adminUpdateLocation(locationId, request)).thenReturn(expectedResponse);

        mockMvc.perform(put(ADMIN_LOCATION_PATH + "/{id}", locationId)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value(locationName))
               .andDo(document("admin/update-location",
                       pathParameters(
                               parameterWithName("id").description("ID de la ubicación a modificar.")
                       ),
                       requestFields(locationAdminRequestFields),
                       responseFields(locationResponseFields)
               ));
    }

    @Test
    void deleteLocation() throws Exception {

        int locationId = 1;
        doNothing().when(adminLocationService).adminDeleteLocation(eq(locationId));

        mockMvc.perform(delete(ADMIN_LOCATION_PATH + "/{id}", locationId))
               .andExpect(status().isNoContent())
               .andDo(document("admin/delete-location",
                       pathParameters(
                               parameterWithName("id").description("ID de la ubicación a eliminar.")
                       )
               ));
    }
}