package talento.futuro.iotapidev.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@Getter
	@RequiredArgsConstructor
	static class ErrorResponse{
		private final String message;
		private final LocalDateTime timestamp;
	}
	
    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCompanyNotFoundException(CompanyNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
        		new ErrorResponse(ex.getMessage(), LocalDateTime.now()));
    }
    
    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLocationNotFoundException(LocationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
        		new ErrorResponse(ex.getMessage(), LocalDateTime.now()));
    }
    
    @ExceptionHandler(SensorNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSensorNotFoundException(SensorNotFoundException ex) {
    	 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
         		new ErrorResponse(ex.getMessage(), LocalDateTime.now()));
    }
    
    @ExceptionHandler(DuplicatedLocationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatedLocationException(DuplicatedLocationException ex) {
    	 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
         		new ErrorResponse(ex.getMessage(), LocalDateTime.now()));
    }
    
    @ExceptionHandler(DuplicatedSensorException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatedSensorException(DuplicatedSensorException ex) {
    	 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
         		new ErrorResponse(ex.getMessage(), LocalDateTime.now()));
    }
    
}