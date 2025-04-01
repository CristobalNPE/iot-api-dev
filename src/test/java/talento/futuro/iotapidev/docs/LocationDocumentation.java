package talento.futuro.iotapidev.docs;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class LocationDocumentation {


    public static final FieldDescriptor[] locationAdminRequestFields = new FieldDescriptor[]{
            fieldWithPath("companyId")
                    .description("ID numérico de la compañía a la que se asociará esta ubicación. Obligatorio.")
                    .type(JsonFieldType.NUMBER),
            fieldWithPath("name")
                    .description("Nombre descriptivo para la nueva ubicación (ej: 'Almacén Principal', 'Oficina Norte'). Obligatorio, no puede estar vacío.")
                    .type(JsonFieldType.STRING),
            fieldWithPath("country")
                    .description("País donde se encuentra físicamente la ubicación. Obligatorio, no puede estar vacío.")
                    .type(JsonFieldType.STRING),
            fieldWithPath("city")
                    .description("Ciudad donde se encuentra físicamente la ubicación. Obligatorio, no puede estar vacío.")
                    .type(JsonFieldType.STRING),
            fieldWithPath("meta")
                    .description("Campo de texto libre para añadir metadatos o descripciones adicionales sobre la ubicación (ej: 'Planta 3, Sector B'). Obligatorio, no puede estar vacío.")
                    .type(JsonFieldType.STRING)
    };

    public static final FieldDescriptor[] locationRequestFields = new FieldDescriptor[]{
            fieldWithPath("name")
                    .description("Nombre descriptivo para la nueva ubicación (ej: 'Almacén Principal', 'Oficina Norte'). Obligatorio, no puede estar vacío.")
                    .type(JsonFieldType.STRING),
            fieldWithPath("country")
                    .description("País donde se encuentra físicamente la ubicación. Obligatorio, no puede estar vacío.")
                    .type(JsonFieldType.STRING),
            fieldWithPath("city")
                    .description("Ciudad donde se encuentra físicamente la ubicación. Obligatorio, no puede estar vacío.")
                    .type(JsonFieldType.STRING),
            fieldWithPath("meta")
                    .description("Campo de texto libre para añadir metadatos o descripciones adicionales sobre la ubicación (ej: 'Planta 3, Sector B'). Obligatorio, no puede estar vacío.")
                    .type(JsonFieldType.STRING)
    };

    public static final FieldDescriptor[] locationResponseFields = new FieldDescriptor[]{
            fieldWithPath("id")
                    .description("Identificador único asignado a la ubicación por el sistema después de su creación.")
                    .type(JsonFieldType.NUMBER),
            fieldWithPath("name")
                    .description("Nombre de la ubicación.")
                    .type(JsonFieldType.STRING),
            fieldWithPath("country")
                    .description("País de la ubicación.")
                    .type(JsonFieldType.STRING),
            fieldWithPath("city")
                    .description("Ciudad de la ubicación.")
                    .type(JsonFieldType.STRING),
            fieldWithPath("meta")
                    .description("Metadatos o descripción adicional de la ubicación.")
                    .type(JsonFieldType.STRING)
    };

    public static final FieldDescriptor[] locationListResponseFields = new FieldDescriptor[]{
            fieldWithPath("[]").description("Array JSON que contiene todas las ubicaciones obtenibles.")
                    .type(JsonFieldType.ARRAY),
            fieldWithPath("[].id")
                    .description("Identificador único asignado a la ubicación por el sistema después de su creación.")
                    .type(JsonFieldType.NUMBER),
            fieldWithPath("[].name")
                    .description("Nombre de la ubicación.")
                    .type(JsonFieldType.STRING),
            fieldWithPath("[].country")
                    .description("País de la ubicación.")
                    .type(JsonFieldType.STRING),
            fieldWithPath("[].city")
                    .description("Ciudad de la ubicación.")
                    .type(JsonFieldType.STRING),
            fieldWithPath("[].meta")
                    .description("Metadatos o descripción adicional de la ubicación.")
                    .type(JsonFieldType.STRING)
    };

    public static final FieldDescriptor[] locationListResponseFieldsPaginated = new FieldDescriptor[]{
            fieldWithPath("content[]").description("Array JSON que contiene todas las ubicaciones obtenibles.")
                    .type(JsonFieldType.ARRAY),
            fieldWithPath("content[].id")
                    .description("Identificador único asignado a la ubicación por el sistema después de su creación.")
                    .type(JsonFieldType.NUMBER),
            fieldWithPath("content[].name")
                    .description("Nombre de la ubicación.")
                    .type(JsonFieldType.STRING),
            fieldWithPath("content[].country")
                    .description("País de la ubicación.")
                    .type(JsonFieldType.STRING),
            fieldWithPath("content[].city")
                    .description("Ciudad de la ubicación.")
                    .type(JsonFieldType.STRING),
            fieldWithPath("content[].meta")
                    .description("Metadatos o descripción adicional de la ubicación.")
                    .type(JsonFieldType.STRING)
    };
}
