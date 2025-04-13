package talento.futuro.iotapidev.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import talento.futuro.iotapidev.dto.Payload;
import talento.futuro.iotapidev.exception.InvalidJSONException;
import talento.futuro.iotapidev.exception.InvalidMessageTypeException;
import talento.futuro.iotapidev.exception.InvalidSensorApiKeyException;
import talento.futuro.iotapidev.exception.PayloadValidationException;
import talento.futuro.iotapidev.model.Sensor;
import talento.futuro.iotapidev.model.SensorData;
import talento.futuro.iotapidev.repository.SensorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PayloadProcessorTest {

    @Mock
    private Validator validator;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SensorRepository sensorRepository;


    //mocks for JMS message typee
    @Mock
    private TextMessage mockTextMessage;
    @Mock
    private BytesMessage mockBytesMessage;
    @Mock
    private Message mockUnsupportedMessage;

    @InjectMocks
    private PayloadProcessor payloadProcessor;

    // captors
    @Captor
    private ArgumentCaptor<Payload> payloadCaptor;
    @Captor
    private ArgumentCaptor<SensorData> sensorDataCaptor;
    @Captor
    private ArgumentCaptor<Errors> errorsCaptor;

    // test data
    private Sensor testSensor;

    private String validApiKey;
    private String invalidApiKey;

    private Map<String, Object> measurement1;
    private Map<String, Object> measurement2;
    private Map<String, Object> measurementInvalidDatetime;
    private Map<String, Object> measurementNullDatetime;

    private Payload validPayloadDto;
    private Payload payloadInvalidApiKeyDto;
    private Payload payloadEmptyDataDto;
    private Payload payloadMixedValidityDto;

    private String validPayloadJsonString;
    private String invalidJsonString;

    @BeforeEach
    void setUp() {
        ObjectMapper setupMapper = new ObjectMapper();

        validApiKey = "valid-api-key-123";
        invalidApiKey = "invalid-api-key-456";

        testSensor = Sensor.builder()
                           .id(1)
                           .name("Test Sensor")
                           .apiKey(validApiKey)
                           .sensorData(new ArrayList<>())
                           .build();

        long timestamp1 = System.currentTimeMillis();
        long timestamp2 = timestamp1 + 1000;
        measurement1 = Map.of("datetime", timestamp1, "temperature", 25.5, "humidity", 60);
        measurement2 = Map.of("datetime", timestamp2, "temperature", 26.0, "humidity", 61);
        measurementInvalidDatetime = Map.of("datetime", "NaN", "value", 10);
        measurementNullDatetime = Map.of("value", 20);

        validPayloadDto = new Payload(validApiKey, List.of(measurement1, measurement2));
        payloadInvalidApiKeyDto = new Payload(invalidApiKey, List.of(measurement1));
        payloadEmptyDataDto = new Payload(validApiKey, List.of());
        payloadMixedValidityDto = new Payload(validApiKey, List.of(measurement1, measurementInvalidDatetime, measurementNullDatetime, measurement2));

        try {
            validPayloadJsonString = setupMapper.writeValueAsString(validPayloadDto);
            System.out.println(validPayloadJsonString);
        } catch (JsonProcessingException e) {
            fail("Setup failed: Error serializing payload", e);
        }
        invalidJsonString = "{ invalid ugly json [][; 🤮";
    }


    @Nested
    @DisplayName("Tests for extractSensorData(Payload)")
    class ExtractFromPayloadDto {

        @Test
        @DisplayName("Success: Measurements are processed correctly for valid payload")
        void extractSensorData_Payload_Success() {

            when(sensorRepository.findByApiKey(validApiKey)).thenReturn(Optional.of(testSensor));
            when(objectMapper.valueToTree(validPayloadDto.jsonData()))
                    .thenReturn(new ObjectMapper().valueToTree(validPayloadDto.jsonData()));


            payloadProcessor.extractSensorData(validPayloadDto);

            verify(sensorRepository).findByApiKey(validApiKey);
            verify(objectMapper).valueToTree(validPayloadDto.jsonData());

            assertThat(testSensor.getSensorData()).hasSize(2);

            SensorData savedData1 = testSensor.getSensorData().get(0);
            assertThat(savedData1.getSensor()).isEqualTo(testSensor);
            assertThat(savedData1.getTimestamp()).isEqualTo(measurement1.get("datetime"));
            assertThat(savedData1.getData().get("temperature").asDouble()).isEqualTo(25.5);
            assertThat(savedData1.getData().get("humidity").asInt()).isEqualTo(60);

            SensorData savedData2 = testSensor.getSensorData().get(1);
            assertThat(savedData2.getSensor()).isEqualTo(testSensor);
            assertThat(savedData2.getTimestamp()).isEqualTo(measurement2.get("datetime"));
            assertThat(savedData2.getData().get("temperature").asDouble()).isEqualTo(26.0);
            assertThat(savedData2.getData().get("humidity").asInt()).isEqualTo(61);
        }

        @Test
        @DisplayName("Error: Invalid Sensor API Key throws exception")
        void extractSensorData_Payload_InvalidApiKey_ThrowsException() {
            when(sensorRepository.findByApiKey(invalidApiKey)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> payloadProcessor.extractSensorData(payloadInvalidApiKeyDto))
                    .isInstanceOf(InvalidSensorApiKeyException.class);

            verify(sensorRepository).findByApiKey(invalidApiKey);
            verifyNoInteractions(objectMapper);
            assertThat(testSensor.getSensorData()).isEmpty();
        }

        @Test
        @DisplayName("Error: ObjectMapper throws exception during valueToTree() call")
        void extractSensorData_Payload_ObjectMapperError_ThrowsInvalidJSONException() {

            when(sensorRepository.findByApiKey(validApiKey)).thenReturn(Optional.of(testSensor));
            IllegalArgumentException objectMapperException = new IllegalArgumentException("Test exception");
            when(objectMapper.valueToTree(validPayloadDto.jsonData())).thenThrow(objectMapperException);

            assertThatThrownBy(() -> payloadProcessor.extractSensorData(validPayloadDto))
                    .isInstanceOf(InvalidJSONException.class);

            verify(sensorRepository).findByApiKey(validApiKey);
            verify(objectMapper).valueToTree(validPayloadDto.jsonData());
            assertThat(testSensor.getSensorData()).isEmpty();
        }

        @Test
        @DisplayName("Edge Case: Empty jsonData array process successfully, but no data is added")
        void extractSensorData_Payload_EmptyJsonData_SuccessNoDataAdded() {
            when(sensorRepository.findByApiKey(validApiKey)).thenReturn(Optional.of(testSensor));
            when(objectMapper.valueToTree(payloadEmptyDataDto.jsonData()))
                    .thenReturn(new ObjectMapper().valueToTree(payloadEmptyDataDto.jsonData()));

            payloadProcessor.extractSensorData(payloadEmptyDataDto);

            verify(sensorRepository).findByApiKey(validApiKey);
            verify(objectMapper).valueToTree(payloadEmptyDataDto.jsonData());

            assertThat(testSensor.getSensorData()).isEmpty();
        }

        @Test
        @DisplayName("Edge Case: Skips measurements with invalid or missing datetime")
        void extractSensorData_Payload_MixedValidityMeasurements_SkipsInvalid() {
            when(sensorRepository.findByApiKey(validApiKey)).thenReturn(Optional.of(testSensor));
            when(objectMapper.valueToTree(payloadMixedValidityDto.jsonData()))
                    .thenReturn(new ObjectMapper().valueToTree(payloadMixedValidityDto.jsonData()));

            payloadProcessor.extractSensorData(payloadMixedValidityDto);

            verify(sensorRepository).findByApiKey(validApiKey);
            verify(objectMapper).valueToTree(payloadMixedValidityDto.jsonData());

            assertThat(testSensor.getSensorData()).hasSize(2); // 2 valid only

            SensorData savedData1 = testSensor.getSensorData().get(0);
            assertThat(savedData1.getTimestamp()).isEqualTo(measurement1.get("datetime"));
            assertThat(savedData1.getData().get("temperature").asDouble()).isEqualTo(25.5);
            assertThat(savedData1.getData().get("humidity").asInt()).isEqualTo(60);

            SensorData savedData2 = testSensor.getSensorData().get(1);
            assertThat(savedData2.getTimestamp()).isEqualTo(measurement2.get("datetime"));
            assertThat(savedData2.getData().get("temperature").asDouble()).isEqualTo(26.0);
            assertThat(savedData2.getData().get("humidity").asInt()).isEqualTo(61);
        }
    }

    @Nested
    @DisplayName("Tests for extractSensorData(Message)")
    class ExtractFromMessage {

        @Test
        @DisplayName("Success: Valid TextMessage is processed correctly")
        void extractSensorData_TextMessage_ValidPayload_Success() throws Exception {

            when(mockTextMessage.getText()).thenReturn(validPayloadJsonString);
            when(objectMapper.readValue(eq(validPayloadJsonString), eq(Payload.class))).thenReturn(validPayloadDto);

            doNothing().when(validator).validate(eq(validPayloadDto), any(Errors.class)); // we assume no validation errors

            when(sensorRepository.findByApiKey(validApiKey)).thenReturn(Optional.of(testSensor));
            when(objectMapper.valueToTree(validPayloadDto.jsonData()))
                    .thenReturn(new ObjectMapper().valueToTree(validPayloadDto.jsonData()));


            payloadProcessor.extractSensorData(mockTextMessage);

            verify(mockTextMessage).getText();
            verify(objectMapper).readValue(eq(validPayloadJsonString), eq(Payload.class));
            verify(validator).validate(payloadCaptor.capture(), errorsCaptor.capture());
            verify(sensorRepository).findByApiKey(validApiKey);
            verify(objectMapper).valueToTree(validPayloadDto.jsonData());

            assertThat(payloadCaptor.getValue()).isEqualTo(validPayloadDto);
            assertThat(errorsCaptor.getValue().hasErrors()).isFalse(); // todo: check: validation mock worked?

            assertThat(testSensor.getSensorData()).hasSize(2);
            assertThat(testSensor.getSensorData().get(0).getTimestamp()).isEqualTo(measurement1.get("datetime"));
            assertThat(testSensor.getSensorData().get(1).getTimestamp()).isEqualTo(measurement2.get("datetime"));
        }

        @Test
        @DisplayName("Success: Valid BytesMessage processes correctly")
        void extractSensorData_BytesMessage_ValidPayload_Success() throws Exception {
            byte[] payloadBytes = validPayloadJsonString.getBytes();
            when(mockBytesMessage.getBodyLength()).thenReturn((long) payloadBytes.length);


            // ***  does not work, because readBytes has a side effect (it fills the byte array with msg data,
            // not only returns an integer
//            when(mockBytesMessage.readBytes(any(byte[].class))).thenReturn(payloadBytes.length);

            doAnswer(invocation -> {
                byte[] bytesArray = invocation.getArgument(0);
                System.arraycopy(payloadBytes, 0, bytesArray, 0, payloadBytes.length);
                return payloadBytes.length;
            }).when(mockBytesMessage).readBytes(any(byte[].class));

            when(objectMapper.readValue(eq(validPayloadJsonString), eq(Payload.class))).thenReturn(validPayloadDto);
            doNothing().when(validator).validate(eq(validPayloadDto), any(Errors.class)); // ~~no validation errors
            when(sensorRepository.findByApiKey(validApiKey)).thenReturn(Optional.of(testSensor));
            when(objectMapper.valueToTree(validPayloadDto.jsonData()))
                    .thenReturn(new ObjectMapper().valueToTree(validPayloadDto.jsonData()));

            payloadProcessor.extractSensorData(mockBytesMessage);

            verify(mockBytesMessage).getBodyLength();
            verify(mockBytesMessage).readBytes(any(byte[].class));
            verify(objectMapper).readValue(eq(validPayloadJsonString), eq(Payload.class));
            verify(validator).validate(any(Payload.class), any(Errors.class)); // todo: check this (only verifies the call)
            verify(sensorRepository).findByApiKey(validApiKey);
            verify(objectMapper).valueToTree(validPayloadDto.jsonData());
            assertThat(testSensor.getSensorData()).hasSize(2);
        }


        @Test
        @DisplayName("Error: Unsupported Message type throws exception")
        void extractSensorData_UnsupportedMessage_ThrowsException() {

            assertThatThrownBy(() -> payloadProcessor.extractSensorData(mockUnsupportedMessage))
                    .isInstanceOf(InvalidMessageTypeException.class);

            verifyNoInteractions(objectMapper, validator, sensorRepository);
        }

        @Test
        @DisplayName("Error: JMSException during message read re-throws as InvalidMessageTypeException")
        void extractSensorData_TextMessage_JMSExceptionOnGetText_ThrowsException() throws Exception {

            JMSException jmsException = new JMSException("Test read exception");
            when(mockTextMessage.getText()).thenThrow(jmsException);

            assertThatThrownBy(() -> payloadProcessor.extractSensorData(mockTextMessage))
                    .isInstanceOf(InvalidMessageTypeException.class);

            verify(mockTextMessage).getText();
            verifyNoInteractions(objectMapper, validator, sensorRepository);
        }

        @Test
        @DisplayName("Error: Invalid JSON string in message throws exception")
        void extractSensorData_TextMessage_InvalidJson_ThrowsException() throws Exception {
            when(mockTextMessage.getText()).thenReturn(invalidJsonString);
            JsonProcessingException jsonException = mock(JsonProcessingException.class); // can't instantiate its protected
            when(objectMapper.readValue(eq(invalidJsonString), eq(Payload.class))).thenThrow(jsonException);

            assertThatThrownBy(() -> payloadProcessor.extractSensorData(mockTextMessage))
                    .isInstanceOf(InvalidJSONException.class)
                    .hasCause(jsonException);

            verify(mockTextMessage).getText();
            verify(objectMapper).readValue(eq(invalidJsonString), eq(Payload.class));
            verifyNoInteractions(validator, sensorRepository);
            assertThat(testSensor.getSensorData()).isEmpty();
        }

        @Test
        @DisplayName("Error: Payload validation fails throws exception")
        void extractSensorData_TextMessage_ValidationFails_ThrowsException() throws Exception {

            Payload invalidPayloadDto = new Payload(null, List.of(measurement1)); // null api key covered by @NotBlank
            String invalidDtoJSON = new ObjectMapper().writeValueAsString(invalidPayloadDto);

            when(mockTextMessage.getText()).thenReturn(invalidDtoJSON);
            when(objectMapper.readValue(eq(invalidDtoJSON), eq(Payload.class))).thenReturn(invalidPayloadDto);

            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(invalidPayloadDto, "payload");
            bindingResult.rejectValue("apiKey", "NotBlank", "api_key is required"); // needed?

            doAnswer(invocation -> {
                Errors errors = invocation.getArgument(1);
                errors.rejectValue("apiKey", "NotBlank", "api_key is required");
                return null;
            }).when(validator).validate(eq(invalidPayloadDto), any(Errors.class));

            assertThatThrownBy(() -> payloadProcessor.extractSensorData(mockTextMessage))
                    .isInstanceOf(PayloadValidationException.class);

            verify(mockTextMessage).getText();
            verify(objectMapper).readValue(eq(invalidDtoJSON), eq(Payload.class));
            verify(validator).validate(eq(invalidPayloadDto), any(Errors.class));
            verifyNoInteractions(sensorRepository);
            assertThat(testSensor.getSensorData()).isEmpty();
        }

        @Test
        @DisplayName("Error: Invalid Sensor API Key (from message) throws exception")
        void extractSensorData_TextMessage_InvalidApiKey_ThrowsException() throws Exception {

            String invalidApiKeyJson = new ObjectMapper().writeValueAsString(payloadInvalidApiKeyDto);

            when(mockTextMessage.getText()).thenReturn(invalidApiKeyJson);
            when(objectMapper.readValue(eq(invalidApiKeyJson), eq(Payload.class))).thenReturn(payloadInvalidApiKeyDto);
            doNothing().when(validator).validate(eq(payloadInvalidApiKeyDto), any(Errors.class)); // validation ok
            when(sensorRepository.findByApiKey(invalidApiKey)).thenReturn(Optional.empty()); // no api key found

            assertThatThrownBy(() -> payloadProcessor.extractSensorData(mockTextMessage))
                    .isInstanceOf(InvalidSensorApiKeyException.class);

            verify(mockTextMessage).getText();
            verify(objectMapper).readValue(eq(invalidApiKeyJson), eq(Payload.class));
            verify(validator).validate(eq(payloadInvalidApiKeyDto), any(Errors.class));
            verify(sensorRepository).findByApiKey(invalidApiKey);
            assertThat(testSensor.getSensorData()).isEmpty();

        }
    }


}