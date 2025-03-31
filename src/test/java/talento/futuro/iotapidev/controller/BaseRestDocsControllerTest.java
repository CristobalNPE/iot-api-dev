package talento.futuro.iotapidev.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import talento.futuro.iotapidev.model.Company;
import talento.futuro.iotapidev.repository.CompanyRepository;
import talento.futuro.iotapidev.security.SecurityConfig;
import talento.futuro.iotapidev.service.AuthService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@Import(SecurityConfig.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
public abstract class BaseRestDocsControllerTest {


    protected static final String VALID_COMPANY_API_KEY = "valid-company-api-key-123";
    protected static final int MOCK_COMPANY_ID = 1;

    @MockitoBean
    protected CompanyRepository companyRepository;

    @MockitoBean
    private AuthService authService;

    @Autowired
    protected ObjectMapper objectMapper;

    protected MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {


        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                                      .apply(documentationConfiguration(restDocumentation)
                                              .uris().withScheme("http").withHost("localhost").withPort(8080)
                                              .and()
                                              .snippets().withEncoding("UTF-8")
                                              .and().operationPreprocessors()
                                              .withRequestDefaults(prettyPrint())
                                              .withResponseDefaults(prettyPrint())

                                      )
                                      .apply(springSecurity())// use filters
                                      .build();

        // this is for tests that check against an existing company
        Company mockCompany = Company.builder()
                                     .id(MOCK_COMPANY_ID)
                                     .apiKey(VALID_COMPANY_API_KEY)
                                     .name("Base Mock Company :)")
                                     .build();
        when(companyRepository.findByApiKey(eq(VALID_COMPANY_API_KEY)))
                .thenReturn(Optional.of(mockCompany));


        when(authService.getCompanyIdFromContext()).thenReturn(MOCK_COMPANY_ID);
    }
}
