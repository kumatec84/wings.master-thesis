package de.kubbillum.masterthesis.rxcheckwrapper.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.hl7.fhir.r4.model.Binary;
//import org.apache.commons.codec.binary.Base64;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import de.kubbillum.masterthesis.rxcheckwrapper.generated.EDispensierung;
import de.kubbillum.masterthesis.rxcheckwrapper.model.EVerordnungsdaten;
import de.kubbillum.masterthesis.rxcheckwrapper.service.TA7DataExtractor;
import jakarta.xml.bind.annotation.XmlRootElement;
//import de.kubbillum.masterthesis.rxcheckwrapper.generated.EDispensierung;
import lombok.Data;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("/api/rxcheck")
public class RxCheckWrapperController {

	private FhirContext context;
	private IParser parser;

	@Autowired
	TA7DataExtractor ta7DataExtr;

	@PostMapping(value = "/validate", consumes = "application/xml", produces = { "application/json",
			"application/xml" })
	public ResponseEntity<ValidationResult> validatePrescription(@RequestBody EDispensierung eDispensierung) {

		context = FhirContext.forR4();
		parser = context.newXmlParser();

		System.out.println("test: " + eDispensierung.getRezeptId());

		// byte[] decodedBytes =
		// Base64.getDecoder().decode(eDispensierung.getEVerordnung());
		try {
			String decodedString = new String(eDispensierung.getEVerordnung(), "UTF-8");
			BufferedInputStream bis = new BufferedInputStream(
					new ByteArrayInputStream(eDispensierung.getEVerordnung()));
			String bundleString = parseCMS(eDispensierung.getEVerordnung());
			System.out.println("bundleString: " + bundleString);

			if (!bundleString.isBlank()) {
				Bundle bundle = context.newXmlParser().parseResource(Bundle.class, bundleString);
				System.out.println("fhirType: " + bundle.fhirType());
				TA7DataExtractor tA7DataExtractor = new TA7DataExtractor();
				EVerordnungsdaten verordnungsdaten = tA7DataExtractor.getVerordnungsdaten(bundle);
				System.out.println("verordnungsdaten: " + verordnungsdaten);
			}

//			CMSSignedData s = new CMSSignedData(eDispensierung.getEVerordnung());
			// TA7DataExtractor.decodeUnsignedBinary

//			Bundle bundle = parseToBundle(bis);
//			System.out.println(decodedString);

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Dekodiertes Byte-Array in einen String umwandeln
//        String decodedString = new String(decodedBytes);
//        System.out.println(decodedString);
//		byte[] s = Base64.decodeBase64(eDispensierung.getEVerordnung());
//		System.out.println("s: " + s);
//		BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(s));
////		BufferedInputStream bis = new BufferedInputStream(org.apache.commons.io.IOUtils.toInputStream(s, "UTF-8"));
//		Bundle bundle = parseToBundle(bis); 
//		System.out.println(bundle.fhirType());
//		// Hier fügen Sie die Logik zur Validierung des Rezepts ein
		ValidationResult result = new ValidationResult();
		result.setValid(true);
		result.setMessages(List.of("Validation successful"));

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

//	private Bundle decodeUnsignedBinaryToBundle(Binary binary) {
//
//		String bundleString = parseCMS(binary);
//
//		if (!bundleString.isBlank()) {
//			// log.trace(bundleString);
//			return context.newXmlParser().parseResource(Bundle.class, bundleString);
//		}
//
//		return null;
//	}

	public String parseCMS(byte[] pkcs7Data) {
		// byte[] pkcs7Data = binary.getData();
		if (pkcs7Data[0] == '<') {
			return new String(pkcs7Data);
		}

		CMSSignedData signedData;
		try {
			signedData = new CMSSignedData(pkcs7Data);
			CMSProcessableByteArray signedContent = (CMSProcessableByteArray) signedData.getSignedContent();
			byte[] data = (byte[]) signedContent.getContent();

			return new String(data);
		} catch (CMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private Bundle parseToBundle(InputStream is) {
		return parser.parseResource(Bundle.class, is);
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
	@Data
	@NoArgsConstructor
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
