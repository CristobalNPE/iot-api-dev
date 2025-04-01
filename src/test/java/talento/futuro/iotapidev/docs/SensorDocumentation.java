package talento.futuro.iotapidev.docs;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class SensorDocumentation {

    public static final FieldDescriptor[] sensorAdminRequestFields = {
            fieldWithPath("locationId")
                    .description("ID numérico de la ubicación a la que pertenecerá este sensor. Obligatorio.")
                    .type(JsonFieldType.NUMBER),
            fieldWithPath("sensorName")
                    .description("Nombre único y descriptivo para el sensor (ej: 'Temperatura Salón', 'Nivel Tanque Agua'). Obligatorio, no puede estar vacío.")
                    .type(JsonFieldType.STRING),
            fieldWithPath("sensorCategory")
                    .description("Categoría o tipo del sensor (ej: 'Temperatura', 'Humedad', 'Nivel', 'Contador'). Obligatorio, no puede estar vacío.")
                    .type(JsonFieldType.STRING),
            fieldWithPath("sensorMeta")
                    .description("Metadatos adicionales sobre el sensor (ej: 'Modelo DHT22', 'Instalado en Rack 3'). Obligatorio, no puede estar vacío.")
                    .type(JsonFieldType.STRING)
    };

    public static final FieldDescriptor[] sensorResponseFields = {
            fieldWithPath("id")
                    .description("Identificador único asignado al sensor por el sistema.")
                    .type(JsonFieldType.NUMBER),
            fieldWithPath("locationId")
                    .description("ID de la ubicación a la que está asociado el sensor.")
                    .type(JsonFieldType.NUMBER),
            fieldWithPath("name")
                    .description("Nombre del sensor.")
                    .type(JsonFieldType.STRING),
            fieldWithPath("category")
                    .description("Categoría o tipo del sensor.")
                    .type(JsonFieldType.STRING),
            fieldWithPath("meta")
                    .description("Metadatos adicionales del sensor.")
                    .type(JsonFieldType.STRING),
            fieldWithPath("apiKey")
                    .description("Clave API única generada para este sensor. Se usará en el payload al enviar datos.")
                    .type(JsonFieldType.STRING)
    };

    public static final FieldDescriptor[] sensorListResponseFields = {
            fieldWithPath("[]").description("Array JSON que contiene todos los sensores obtenibles."),
            fieldWithPath("[].id").description("ID del sensor."),
            fieldWithPath("[].locationId").description("ID de la ubicación asociada."),
            fieldWithPath("[].name").description("Nombre del sensor."),
            fieldWithPath("[].category").description("Categoría del sensor."),
            fieldWithPath("[].meta").description("Metadatos del sensor."),
            fieldWithPath("[].apiKey").description("API Key del sensor.")
    };

    public static final FieldDescriptor[] sensorListResponseFieldsPaginated = {
            fieldWithPath("content[]").description("Array JSON que contiene todos los sensores obtenibles."),
            fieldWithPath("content[].id").description("ID del sensor."),
            fieldWithPath("content[].locationId").description("ID de la ubicación asociada."),
            fieldWithPath("content[].name").description("Nombre del sensor."),
            fieldWithPath("content[].category").description("Categoría del sensor."),
            fieldWithPath("content[].meta").description("Metadatos del sensor."),
            fieldWithPath("content[].apiKey").description("API Key del sensor.")
    };
}
