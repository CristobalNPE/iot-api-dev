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
import talento.futuro.iotapidev.dto.LocationRequest;
import talento.futuro.iotapidev.dto.LocationResponse;
import talento.futuro.iotapidev.exception.LocationNotFoundException;
import talento.futuro.iotapidev.service.LocationService;
import talento.futuro.iotapidev.utils.WithMockCompany;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static talento.futuro.iotapidev.constants.ApiKeys.COMPANY_API_KEY_HEADER;
import static talento.futuro.iotapidev.docs.LocationDocumentation.*;
import static talento.futuro.iotapidev.docs.SharedDocumentation.Authentication.companyApiKeyHeader;
import static talento.futuro.iotapidev.docs.SharedDocumentation.Authentication.companyApiKeyHeaderInvalid;
import static talento.futuro.iotapidev.docs.SharedDocumentation.Errors.domainErrorResponseFields;
import static talento.futuro.iotapidev.docs.SharedDocumentation.Pagination.getPageResponseFields;
import static talento.futuro.iotapidev.docs.SharedDocumentation.Pagination.paginationRequestParameters;
import static talento.futuro.iotapidev.utils.LocationTestDataFactory.*;

@WebMvcTest(LocationController.class)
class LocationControllerTest extends BaseRestDocsControllerTest {

    private static final String LOCATION_PATH = ApiBase.V1 + ApiPath.LOCATION;

    @MockitoBean
    private LocationService locationService;

    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void getAllLocationsForCurrentCompany_Success() throws Exception {

        Pageable requestedPageable = PageRequest.of(0, 20);
        List<LocationResponse> locationsList = createDefaultLocationResponseList();
        Page<LocationResponse> expectedPage = new PageImpl<>(locationsList, requestedPageable, locationsList.size());
        when(locationService.findAllLocationsForCurrentCompany(any(Pageable.class))).thenReturn(expectedPage);

        mockMvc.perform(get(LOCATION_PATH)
                       .accept(MediaType.APPLICATION_JSON)
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY)) //!! this header is only here to satisfy the docs, the company is already authenticated for this test via @WithMockCompany
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content[0].id").value(expectedPage.getContent().get(0).id()))
               .andExpect(jsonPath("$.content[0].name").value(expectedPage.getContent().get(0).name()))
               .andExpect(jsonPath("$.content[1].name").value(expectedPage.getContent().get(1).name()))
               .andExpect(jsonPath("$.content[2].country").value(expectedPage.getContent().get(2).country()))
               .andDo(document("location/get-all-paged",
                       requestHeaders(companyApiKeyHeader),
                       queryParameters(paginationRequestParameters),
                       responseFields(getPageResponseFields(
                               "Lista paginada de ubicaciones para la compañía", locationListResponseFieldsPaginated)
                       )));
    }

    @Test
    void getAllLocationsForCurrentCompany_InvalidApiKey() throws Exception {

        String invalidApiKey = "invalid-key";

        when(companyRepository.findByApiKey(eq(invalidApiKey))).thenReturn(Optional.empty());

        mockMvc.perform(get(LOCATION_PATH)
                       .header(COMPANY_API_KEY_HEADER, invalidApiKey)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isUnauthorized())
               .andDo(document("location/get-all-locations-invalid-key",
                       requestHeaders(companyApiKeyHeaderInvalid)));
    }

    @Test
    void getAllLocationsForCurrentCompany_MissingApiKey() throws Exception {

        // no config needed
        mockMvc.perform(get(LOCATION_PATH)
                       //.header(COMPANY_API_KEY_HEADER, ... NO HEADER INCLUDED)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isUnauthorized())
               .andDo(document("location/get-all-locations-missing-key"
               ));
    }

    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void getLocationById_Success() throws Exception {

        int locationId = 10;

        LocationResponse expectedResponse = createDefaultLocationResponse(locationId, "Ubicación Específica");
        when(locationService.findLocationByIdForCompany(eq(locationId))).thenReturn(expectedResponse);


        mockMvc.perform(get(LOCATION_PATH + "/{locationId}", locationId)
                       .accept(MediaType.APPLICATION_JSON)
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(locationId))
               .andExpect(jsonPath("$.name").value(expectedResponse.name()))
               .andDo(document("location/get-by-id",
                       requestHeaders(companyApiKeyHeader),
                       pathParameters(
                               parameterWithName("locationId").description("ID numérico de la ubicación a obtener.")
                       ),
                       responseFields(locationResponseFields)
               ));


    }

    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void getLocationById_NotFoundForCompany() throws Exception {//default
        int locationId = 20; //location id that does not belong to company 1 (or does not exist)


        when(locationService.findLocationByIdForCompany(eq(locationId))).thenThrow(new LocationNotFoundException(locationId));

        mockMvc.perform(get(LOCATION_PATH + "/{locationId}", locationId)
                       .accept(MediaType.APPLICATION_JSON)
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY))
               .andExpect(status().isNotFound())
               .andDo(document("location/get-by-id-not-found",
                       requestHeaders(companyApiKeyHeader),
                       pathParameters(
                               parameterWithName("locationId").description("ID de la ubicación no encontrada o no perteneciente a la compañía.")
                       ),
                       responseFields(domainErrorResponseFields)));
    }

    @Test
    void getLocationById_InvalidApiKey() throws Exception {
        int locationId = 10;
        String invalidApiKey = "invalid-key";
        when(companyRepository.findByApiKey(eq(invalidApiKey))).thenReturn(Optional.empty());

        mockMvc.perform(get(LOCATION_PATH + "/{locationId}", locationId)
                       .header(COMPANY_API_KEY_HEADER, invalidApiKey)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isUnauthorized())
               .andDo(document("location/get-by-id-invalid-key",
                       requestHeaders(companyApiKeyHeaderInvalid),
                       pathParameters(
                               parameterWithName("locationId").description("ID de la ubicación solicitada.")
                       )
               ));
    }

    @Test
    void getLocationById_MissingApiKey() throws Exception {
        int locationId = 10;
        mockMvc.perform(get(LOCATION_PATH + "/{locationId}", locationId)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isUnauthorized())
               .andDo(document("location/get-by-id-missing-key"
               ));
    }

    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void createLocationForCurrentCompany_Success() throws Exception {


        LocationRequest request = createDefaultLocationRequest("Nueva Ubicación");
        LocationResponse expectedResponse = createLocationResponseFromRequest(15, request);
        when(locationService.createLocationForCurrentCompany(any(LocationRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post(LOCATION_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request))
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(expectedResponse.id()))
               .andExpect(jsonPath("$.name").value(expectedResponse.name()))
               .andDo(document("location/create",
                       requestHeaders(companyApiKeyHeader),
                       requestFields(locationRequestFields),
                       responseFields(locationResponseFields)
               ));
    }

    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void createLocationForCurrentCompany_ValidationError() throws Exception {


        LocationRequest invalidRequest = new LocationRequest("", "País", "Ciudad", "Meta");

        //no mock needed validations fails before (thanks dto)

        mockMvc.perform(post(LOCATION_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(invalidRequest))
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY))
               .andExpect(status().isBadRequest())
               .andDo(document("location/create-validation-error",
                       requestHeaders(companyApiKeyHeader),
                       requestFields(locationRequestFields),
                       responseFields(
                               fieldWithPath("name").description("Mensaje indicando que el nombre no puede estar vacío.")
                       )));
    }

    @Test
    void createLocationForCurrentCompany_InvalidApiKey() throws Exception {
        String invalidApiKey = "invalid-key";
        when(companyRepository.findByApiKey(eq(invalidApiKey))).thenReturn(Optional.empty());

        mockMvc.perform(post(LOCATION_PATH)
                       .header(COMPANY_API_KEY_HEADER, invalidApiKey))
               .andExpect(status().isUnauthorized())
               .andDo(document("location/create-invalid-key",
                       requestHeaders(companyApiKeyHeaderInvalid)
               ));
    }

    @Test
    void createLocationForCurrentCompany_MissingApiKey() throws Exception {

        mockMvc.perform(post(LOCATION_PATH))
               .andExpect(status().isUnauthorized())
               .andDo(document("location/create-missing-key"
               ));
    }


    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void updateLocation_Success() throws Exception {
        int locationId = 10;


        LocationRequest request = createDefaultLocationRequest("Ubicación Actualizada");
        LocationResponse expectedResponse = createDefaultLocationResponse(locationId, "Ubicación Actualizada");
        when(locationService.updateLocationForCompany(eq(locationId), any(LocationRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(put(LOCATION_PATH + "/{locationId}", locationId)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request))
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(locationId))
               .andExpect(jsonPath("$.name").value(expectedResponse.name()))
               .andDo(document("location/update",
                       requestHeaders(companyApiKeyHeader),
                       pathParameters(
                               parameterWithName("locationId").description("ID de la ubicación a actualizar.")
                       ),
                       requestFields(locationRequestFields),
                       responseFields(locationResponseFields)
               ));
    }

    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void updateLocation_NotFoundForCompany() throws Exception {
        int locationId = 20;


        LocationRequest request = createDefaultLocationRequest("Intento Update");

        when(locationService.updateLocationForCompany(eq(locationId), any(LocationRequest.class)))
                .thenThrow(new LocationNotFoundException(locationId));

        mockMvc.perform(put(LOCATION_PATH + "/{locationId}", locationId)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request))
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY))
               .andExpect(status().isNotFound())
               .andDo(document("location/update-not-found",
                       requestHeaders(companyApiKeyHeader),
                       pathParameters(
                               parameterWithName("locationId").description("ID de la ubicación no encontrada.")
                       ),
                       requestFields(locationRequestFields),
                       responseFields(domainErrorResponseFields)
               ));
    }

    @Test
    void updateLocation_InvalidApiKey() throws Exception {
        int locationId = 10;
        String invalidApiKey = "invalid-key";
        when(companyRepository.findByApiKey(eq(invalidApiKey))).thenReturn(Optional.empty());

        mockMvc.perform(put(LOCATION_PATH + "/{locationId}", locationId)
                       .header(COMPANY_API_KEY_HEADER, invalidApiKey)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isUnauthorized())
               .andDo(document("location/update-invalid-key",
                       requestHeaders(companyApiKeyHeaderInvalid),
                       pathParameters(
                               parameterWithName("locationId").description("ID de la ubicación a modificar.")
                       )
               ));
    }

    @Test
    void updateLocation_MissingApiKey() throws Exception {

        mockMvc.perform(put(LOCATION_PATH).with(csrf())
                                          .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isUnauthorized())
               .andDo(document("location/update-missing-key"
               ));
    }


    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void deleteLocation_Success() throws Exception {
        int locationId = 10;

        doNothing().when(locationService).deleteLocationForCompany(eq(locationId));

        mockMvc.perform(delete(LOCATION_PATH + "/{locationId}", locationId)
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY))
               .andExpect(status().isNoContent())
               .andDo(document("location/delete",
                       requestHeaders(companyApiKeyHeader),
                       pathParameters(
                               parameterWithName("locationId").description("ID de la ubicación a eliminar.")
                       )
               ));
    }

    @Test
    @WithMockCompany(apiKey = VALID_COMPANY_API_KEY, companyId = MOCK_COMPANY_ID)
    void deleteLocation_NotFoundForCompany() throws Exception {
        int locationId = 20;

        doThrow(new LocationNotFoundException(locationId)).when(locationService).deleteLocationForCompany(eq(locationId));

        mockMvc.perform(delete(LOCATION_PATH + "/{locationId}", locationId)
                       .header(COMPANY_API_KEY_HEADER, VALID_COMPANY_API_KEY))
               .andExpect(status().isNotFound())
               .andDo(document("location/delete-not-found",
                       requestHeaders(companyApiKeyHeader),
                       pathParameters(
                               parameterWithName("locationId").description("ID de la ubicación no encontrada.")
                       ),
                       responseFields(domainErrorResponseFields)
               ));
    }

    @Test
    void deleteLocation_InvalidApiKey() throws Exception {
        int locationId = 10;
        String invalidApiKey = "invalid-key";
        when(companyRepository.findByApiKey(eq(invalidApiKey))).thenReturn(Optional.empty());

        mockMvc.perform(put(LOCATION_PATH + "/{locationId}", locationId)
                       .header(COMPANY_API_KEY_HEADER, invalidApiKey))
               .andExpect(status().isUnauthorized())
               .andDo(document("location/delete-invalid-key",
                       requestHeaders(companyApiKeyHeaderInvalid),
                       pathParameters(
                               parameterWithName("locationId").description("ID de la ubicación a eliminar.")
                       )
               ));
    }

    @Test
    void deleteLocation_MissingApiKey() throws Exception {

        mockMvc.perform(put(LOCATION_PATH).with(csrf()))
               .andExpect(status().isUnauthorized())
               .andDo(document("location/delete-missing-key"
               ));
    }


}