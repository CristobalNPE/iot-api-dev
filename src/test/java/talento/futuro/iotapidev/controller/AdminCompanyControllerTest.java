package talento.futuro.iotapidev.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import talento.futuro.iotapidev.constants.ApiBase;
import talento.futuro.iotapidev.constants.ApiPath;
import talento.futuro.iotapidev.dto.CompanyRequest;
import talento.futuro.iotapidev.dto.CompanyResponse;
import talento.futuro.iotapidev.exception.NotFoundException;
import talento.futuro.iotapidev.service.CompanyService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static talento.futuro.iotapidev.docs.CompanyDocumentation.*;
import static talento.futuro.iotapidev.docs.SharedDocumentation.Errors.domainErrorResponseFields;
import static talento.futuro.iotapidev.utils.CompanyTestDataFactory.createCompanyResponse;
import static talento.futuro.iotapidev.utils.CompanyTestDataFactory.createDefaultCompanyResponseList;
import static talento.futuro.iotapidev.utils.TestUtils.generateApiKeyForTests;

@WebMvcTest(AdminCompanyController.class)
class AdminCompanyControllerTest extends BaseRestDocsControllerTest {

    private static final String ADMIN_COMPANY_PATH = ApiBase.V1 + ApiPath.ADMIN + ApiPath.COMPANY;

    @MockitoBean
    private CompanyService companyService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCompany() throws Exception {
        CompanyRequest request = new CompanyRequest("Compañía de Prueba");

        CompanyResponse expectedResponse = createCompanyResponse(1, "Compañía de Prueba");

        when(companyService.createCompany(any(CompanyRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post(ADMIN_COMPANY_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(expectedResponse.id()))
               .andExpect(jsonPath("$.name").value(expectedResponse.name()))
               .andExpect(jsonPath("$.apiKey").value(expectedResponse.apiKey()))
               .andDo(document("admin/create-company",
                       requestFields(companyRequestFields),
                       responseFields(companyResponseFields)));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCompanies() throws Exception { //TODO: change when we have pagination

        List<CompanyResponse> expectedResponse = createDefaultCompanyResponseList();
        when(companyService.getAll()).thenReturn(expectedResponse);

        mockMvc.perform(get(ADMIN_COMPANY_PATH)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(expectedResponse.size())))
               .andExpect(jsonPath("$[0].id").value(expectedResponse.get(0).id()))
               .andExpect(jsonPath("$[0].name").value(expectedResponse.get(0).name()))
               .andExpect(jsonPath("$[0].apiKey").value(expectedResponse.get(0).apiKey()))
               .andExpect(jsonPath("$[1].name").value(expectedResponse.get(1).name()))
               .andDo(document("admin/get-all-companies",
                       responseFields(companyListResponseFields)
               ));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCompanyById_Found() throws Exception {
        int companyId = 1;

        CompanyResponse expectedResponse = createCompanyResponse(companyId, "Compañía Uno");
        when(companyService.getById(eq(companyId))).thenReturn(expectedResponse);

        mockMvc.perform(get(ADMIN_COMPANY_PATH + "/{id}", companyId)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(companyId))
               .andDo(document("admin/get-company-by-id",
                       pathParameters(
                               parameterWithName("id").description("El ID numérico de la compañía a buscar.")
                       ),
                       responseFields(companyResponseFields)
               ));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCompanyById_NotFound() throws Exception {

        int companyId = 9999;
        when(companyService.getById(eq(companyId))).thenThrow(new NotFoundException("Company", companyId));

        mockMvc.perform(get(ADMIN_COMPANY_PATH + "/{id}", companyId)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andDo(document("admin/get-company-by-id-not-found",
                       pathParameters(
                               parameterWithName("id").description("El ID de la compañía que no fue encontrada.")
                       ),
                       responseFields(domainErrorResponseFields)
               ));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCompany() throws Exception {
        int companyId = 1;

        String apikey = generateApiKeyForTests();
        CompanyRequest request = new CompanyRequest("Nombre Actualizado");
        CompanyResponse expectedResponse = new CompanyResponse(companyId, "Nombre Actualizado", apikey);

        when(companyService.updateCompany(eq(companyId), any(CompanyRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(put(ADMIN_COMPANY_PATH + "/{id}", companyId)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Nombre Actualizado"))
               .andDo(document("admin/update-company",
                       pathParameters(
                               parameterWithName("id").description("ID de la compañía a modificar.")
                       ),
                       requestFields(companyRequestFields),
                       responseFields(companyResponseFields)
               ));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCompany() throws Exception {

        int companyId = 1;
        doNothing().when(companyService).deleteById(eq(companyId));

        mockMvc.perform(delete(ADMIN_COMPANY_PATH + "/{id}", companyId)
               )
               .andExpect(status().isNoContent())
               .andDo(document("admin/delete-company",
                       pathParameters(
                               parameterWithName("id").description("ID de la compañía a eliminar.")
                       )
               ));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCompany_ValidationError() throws Exception {

        CompanyRequest invalidRequest = new CompanyRequest(null); // Todo check for "" if @NotBlank

        mockMvc.perform(post(ADMIN_COMPANY_PATH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(invalidRequest)))
               .andExpect(status().isBadRequest())
               .andDo(document("admin/create-company-validation-error",
                       responseFields(
                               fieldWithPath("companyName")
                                       .description("CompanyName can't be empty.") //TODO: CHECK THIS, and review the last globalExceptionHandler
                                       .type(JsonFieldType.STRING)
                       )
               ));
    }


}