package talento.futuro.iotapidev.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import talento.futuro.iotapidev.constants.ApiBase;
import talento.futuro.iotapidev.constants.ApiPath;
import talento.futuro.iotapidev.dto.CompanyRequest;
import talento.futuro.iotapidev.dto.CompanyResponse;
import talento.futuro.iotapidev.security.CompanyApiKeyFilter;
import talento.futuro.iotapidev.service.CompanyService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCompanyController.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@ExtendWith(RestDocumentationExtension.class)
class AdminCompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompanyService companyService;

    @MockitoBean
    private CompanyApiKeyFilter filter;

    @Autowired
    private ObjectMapper objectMapper;


    //TODO: move to ApiDocumentationSnippets
    private final FieldDescriptor[] companyRequestFields = new FieldDescriptor[]{
            fieldWithPath("companyName").description("Nombre deseado para la compañía. Campo obligatorio.")
    };

    private final FieldDescriptor[] companyResponseFields = new FieldDescriptor[]{
            fieldWithPath("id").description("Identificador único de la compañía asignado por el sistema."),
            fieldWithPath("name").description("Nombre de la compañía."),
            fieldWithPath("apiKey").description("Clave API única generada para esta compañía. Se usará para autenticar operaciones relacionadas.")
    };

    private final FieldDescriptor[] companyListResponseFields = new FieldDescriptor[]{
            fieldWithPath("[]").description("Array JSON que contiene los objetos de las compañías."),
            fieldWithPath("[].id").description("ID de la compañía."),
            fieldWithPath("[].name").description("Nombre de la compañía."),
            fieldWithPath("[].apiKey").description("API Key de la compañía.")
    };

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                                      .apply(documentationConfiguration(restDocumentation)
                                              .uris().withScheme("http").withHost("localhost").withPort(8080)
                                              .and()
                                              .snippets().withEncoding("UTF-8") // UTF-8 para español
                                      )
                                      .alwaysDo(document("{method-name}",
                                              preprocessRequest(prettyPrint()),
                                              preprocessResponse(prettyPrint())))
                                      .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCompany() throws Exception {
        CompanyRequest request = new CompanyRequest("Test Company");

        CompanyResponse expectedResponse = new CompanyResponse(1, "Test Company", "150296ada94f4ce7b676245bcaad5fb2");
        when(companyService.createCompany(any(CompanyRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post(ApiBase.V1 + ApiPath.ADMIN + ApiPath.COMPANY)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.name").value("Test Company"))
               .andExpect(jsonPath("$.apiKey").value("150296ada94f4ce7b676245bcaad5fb2"))
               // docs
               .andDo(document("admin/create-company",
                       preprocessRequest(prettyPrint()),
                       preprocessResponse(prettyPrint()),
                       requestFields(
                               companyRequestFields
                       ),
                       responseFields(
                               companyResponseFields
                       )));

    }
}