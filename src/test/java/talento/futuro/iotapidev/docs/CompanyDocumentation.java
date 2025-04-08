package talento.futuro.iotapidev.docs;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompanyDocumentation {

    public static final FieldDescriptor[] companyRequestFields = new FieldDescriptor[]{
            fieldWithPath("companyName").description("Nombre deseado para la compañía. Campo obligatorio.")
                    .type(JsonFieldType.STRING),
    };

    public static final FieldDescriptor[] companyResponseFields = new FieldDescriptor[]{
            fieldWithPath("id").description("Identificador único de la compañía asignado por el sistema.")
                    .type(JsonFieldType.NUMBER),
            fieldWithPath("name").description("Nombre de la compañía.")
                    .type(JsonFieldType.STRING),
            fieldWithPath("apiKey").description("Clave API única generada para esta compañía. Se usará para autenticar operaciones relacionadas.")
                    .type(JsonFieldType.STRING)
    };

    public static final FieldDescriptor[] companyListResponseFields = new FieldDescriptor[]{
            fieldWithPath("[]").description("Array JSON que contiene todas las compañías registradas.").type(JsonFieldType.ARRAY),
            fieldWithPath("[].id").description("ID de la compañía.").type(JsonFieldType.NUMBER),
            fieldWithPath("[].name").description("Nombre de la compañía.").type(JsonFieldType.STRING),
            fieldWithPath("[].apiKey").description("API Key de la compañía.").type(JsonFieldType.STRING)
    };

    public static final FieldDescriptor[] companyListResponseFieldsPaginated = new FieldDescriptor[]{
            fieldWithPath("content[]").description("Array JSON que contiene todas las compañías obtenibles."),
            fieldWithPath("content[].id").description("ID de la compañía."),
            fieldWithPath("content[].name").description("Nombre de la compañía."),
            fieldWithPath("content[].apiKey").description("API Key de la compañía.")
    };
}
