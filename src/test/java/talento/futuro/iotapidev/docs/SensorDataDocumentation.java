package talento.futuro.iotapidev.docs;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

public class SensorDataDocumentation {

    public static final FieldDescriptor[] payloadRequestFields = {
            fieldWithPath("api_key")
                    .description("La API Key única del *sensor* que está enviando los datos. Obligatorio.")
                    .type(JsonFieldType.STRING),

            fieldWithPath("json_data")
                    .description("Un array de objetos JSON, donde cada objeto representa una medición.")
                    .type(JsonFieldType.ARRAY),

            fieldWithPath("json_data[].datetime")
                    .description("Timestamp EPOCH (segundos o milisegundos) de la medición.")
                    .type(JsonFieldType.NUMBER),

            subsectionWithPath("json_data[]").ignored() // other fields are unknown and not documented?
    };

    public static final FieldDescriptor[] sensorDataResponseFields = {
            fieldWithPath("sensorId")
                    .description("ID del sensor al que pertenecen estos datos.")
                    .type(JsonFieldType.NUMBER),
            subsectionWithPath("sensorData")
                    .description("Objeto JSON que contiene la medición completa tal como se recibió.")
                    .type(JsonFieldType.OBJECT),
            fieldWithPath("sensorData.datetime").description("Timestamp EPOCH de la medición.").type(JsonFieldType.NUMBER).optional(),

    };

    public static final FieldDescriptor[] sensorDataListResponseFieldsPaginated = {
            fieldWithPath("content[].sensorId").description("ID del sensor.").type(JsonFieldType.NUMBER),
            subsectionWithPath("content[].sensorData").description("Objeto JSON con la medición.").type(JsonFieldType.OBJECT)
    };
}