package talento.futuro.iotapidev.docs;

import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.ParameterDescriptor;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static talento.futuro.iotapidev.constants.ApiKeys.COMPANY_API_KEY_HEADER;
import static talento.futuro.iotapidev.constants.ApiKeys.COMPANY_API_KEY_PARAM;

public class SharedDocumentation {

    public static class Errors {

        public static final FieldDescriptor[] domainErrorResponseFields = {
                fieldWithPath("timestamp").description("La fecha y hora en que se generó el error."),
                fieldWithPath("message").description("El mensaje descriptivo del error.")

                //
//            fieldWithPath("status").description("El código de estado HTTP."),
//            fieldWithPath("error").description("El nombre del error."),
//            fieldWithPath("path").description("La ruta a la que se hizo la solicitud."),
//            fieldWithPath("details").description("Un objeto JSON que contiene detalles adicionales sobre el error.")
        };

        public static FieldDescriptor[] validationErrorResponseFields(String fieldName, String errorMessage) {
            return new FieldDescriptor[]{
                    fieldWithPath(fieldName).description(errorMessage).type(JsonFieldType.STRING)
            };
        }
    }

    public static class Pagination {

        public static final ParameterDescriptor[] paginationRequestParameters = {
                parameterWithName("page").optional().description("Número de página (0-indexado). Defecto: 0."),
                parameterWithName("size").optional().description("Tamaño de página. Defecto: 20."),
                parameterWithName("sort").optional().description("Ordenación: propiedad[,asc|desc]. Ej: 'name,desc'."),
        };

        public static FieldDescriptor[] getPageResponseFields(String contentDescription, FieldDescriptor... contentFields) {
            FieldDescriptor[] pageFields = {
                    fieldWithPath("content[]").description(contentDescription),
                    fieldWithPath("page").description("Objeto que contiene la información de paginación."),
                    fieldWithPath("page.size").description("Número máximo de elementos por página."),
                    fieldWithPath("page.number").description("Número de página actual (0-indexado)."),
                    fieldWithPath("page.totalElements").description("Número total de elementos disponibles en todas las páginas."),
                    fieldWithPath("page.totalPages").description("Número total de páginas disponibles.")
            };

            FieldDescriptor[] allFields = new FieldDescriptor[pageFields.length + contentFields.length];

            System.arraycopy(pageFields, 0, allFields, 0, pageFields.length);
            System.arraycopy(contentFields, 0, allFields, pageFields.length, contentFields.length);

            return allFields;
        }

        public static ParameterDescriptor[] getQueryParametersWithPagination(ParameterDescriptor... specificParameters) {
            int paginationParamCount = paginationRequestParameters.length;
            int specificParamCount = specificParameters.length;
            ParameterDescriptor[] allParams = new ParameterDescriptor[paginationParamCount + specificParamCount];

            System.arraycopy(paginationRequestParameters, 0, allParams, 0, paginationParamCount);

            System.arraycopy(specificParameters, 0, allParams, paginationParamCount, specificParamCount);

            return allParams;
        }

    }

    public static class Authentication {

        public static HeaderDescriptor[] companyApiKeyHeader = {
                headerWithName(COMPANY_API_KEY_HEADER)
                        .description("Opción 1: API Key de la compañía propietaria. (Alternativa al parámetro de consulta '" + COMPANY_API_KEY_PARAM + "').")
                        .optional()
        };


        public static HeaderDescriptor[] companyApiKeyHeaderInvalid = {
                headerWithName(COMPANY_API_KEY_HEADER)
                        .description("API Key inválida o no encontrada.").optional()
        };



}
}
