package de.kubbillum.masterthesis.rxcheckwrapper.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonFormat;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import de.kubbillum.masterthesis.rxcheckwrapper.generated.EDispensierung;
import jakarta.xml.bind.annotation.XmlRootElement;
//import de.kubbillum.masterthesis.rxcheckwrapper.generated.EDispensierung;
//import lombok.Data;
//import lombok.NoArgsConstructor;

@RestController
@RequestMapping("/api/rxcheck")
public class RxCheckWrapperController {

	@PostMapping(value = "/validate", consumes = "application/xml", produces = { "application/json",
			"application/xml" })
	public ResponseEntity<ValidationResult> validatePrescription(@RequestBody EDispensierung eDispensierung) {
		System.out.println("test: " + eDispensierung.getRezeptId());
		// Hier fügen Sie die Logik zur Validierung des Rezepts ein
		ValidationResult result = new ValidationResult();
		result.setValid(true);
		result.setMessages(List.of("Validation successful"));

		return new ResponseEntity<>(result, HttpStatus.OK);
	}



	@GetMapping(value = "/result/{id}", produces = { "application/json", "application/xml" })
	public ResponseEntity<ValidationResult> getValidationResult(@PathVariable String id) {
		// Hier fügen Sie die Logik ein, um das Validierungsergebnis zu erhalten
		ValidationResult result = new ValidationResult();
		result.setValid(true);
		result.setMessages(List.of("Validation result for id: " + id));

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	// Beispiel-Datenklassen
	//@Data
	//@NoArgsConstructor
	@XmlRootElement(name = "ValidationResult")
	public static class ValidationResult {
		private boolean isValid;
		private List<String> messages;
		public void setValid(boolean b) {
			// TODO Auto-generated method stub
			
		}
		public void setMessages(List<String> of) {
			// TODO Auto-generated method stub
			
		}
	}
}
