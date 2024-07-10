package de.kubbillum.masterthesis.rxcheckwrapper.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.fhirpath.IFhirPath;
import ca.uhn.fhir.parser.IParser;
import de.kubbillum.masterthesis.rxcheckwrapper.model.EAbgabedaten;
import de.kubbillum.masterthesis.rxcheckwrapper.model.EQuittungsdaten;
import de.kubbillum.masterthesis.rxcheckwrapper.model.ERezept;
import de.kubbillum.masterthesis.rxcheckwrapper.model.EVerordnungsdaten;
import de.kubbillum.masterthesis.rxcheckwrapper.service.fhirResourceExtractors.*;
import de.kubbillum.masterthesis.rxcheckwrapper.service.fhirResourceExtractors.BundleExtraktor;
import de.kubbillum.masterthesis.rxcheckwrapper.service.fhirResourceExtractors.CommonExtraktor;
import de.kubbillum.masterthesis.rxcheckwrapper.service.fhirResourceExtractors.MedicationExtraktor;
import de.kubbillum.masterthesis.rxcheckwrapper.service.fhirResourceExtractors.VerordnungsdatenExtraktor;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.hl7.fhir.r4.model.Base64BinaryType;
import org.hl7.fhir.r4.model.Binary;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Coverage;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.Dosage;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Invoice;
import org.hl7.fhir.r4.model.Invoice.InvoiceLineItemComponent;
import org.hl7.fhir.r4.model.Invoice.InvoiceLineItemPriceComponentComponent;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.Medication.MedicationIngredientComponent;
import org.hl7.fhir.r4.model.MedicationDispense;
import org.hl7.fhir.r4.model.MedicationDispense.MedicationDispenseSubstitutionComponent;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestDispenseRequestComponent;
import org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestSubstitutionComponent;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Money;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Practitioner.PractitionerQualificationComponent;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Ratio;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TA7DataExtractor {

	private static final String EMPFAENGER_IK_URL = "https://fhir.gkvsv.de/StructureDefinition/GKVSV_EX_TA7_IK_Empfaenger";
	private static final String KOSTENTRAEGER_IK_URL = "https://fhir.gkvsv.de/StructureDefinition/GKVSV_EX_TA7_IK_Kostentraeger";
	private static final String DAV_EX_ERP_ZAEHLER = "http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-EX-ERP-Zaehler";
	private static final String DAV_EX_ERP_VERTAGSKENNZEICHEN = "http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-EX-ERP-Vertragskennzeichen";
	private static final String KBV_EX_ERP_MEDICATION_CATEGORY = "https://fhir.kbv.de/StructureDefinition/KBV_EX_ERP_Medication_Category";
	private static final String KBV_EX_FOR_STATUSCOPAYMENT = "https://fhir.kbv.de/StructureDefinition/KBV_EX_FOR_StatusCoPayment";
	private static final String KBV_EX_FOR_ACCIDENT = "https://fhir.kbv.de/StructureDefinition/KBV_EX_FOR_Accident";
	private static final String DMP_KENNZEICHEN = "http://fhir.de/StructureDefinition/gkv/dmp-kennzeichen";
	private static final String NOT_ONLY_DIGITS = "Der String {} enthält nicht nur Ziffern";
	private static final String VALID_DIGIT_SEQUENCE = "Ungültige Ziffernfolge: {}";
	private static final String ID_VERORDNUNGSDATEN = "Verordnung";
	private static final String ID_QUITTUNGSDATEN = "Quittung";
	private static final String ID_ABGABEDATEN = "Abgabedaten";
	private static final String ID_ABRECHNUNGSDATEN = "Abrechnungsdaten";

	private FhirContext context;

	private final IParser parser;

	private final CommonExtraktor commonExtraktor = new CommonExtraktor();
	private final VerordnungsdatenExtraktor verordnungsdatenExtraktor = new VerordnungsdatenExtraktor(commonExtraktor);
	private final MedicationExtraktor medicationExtraktor = new MedicationExtraktor(commonExtraktor);
	BundleExtraktor bundleExtraktor = new BundleExtraktor(commonExtraktor);

	public TA7DataExtractor() {
		log.debug("loading context for r4");
		context = FhirContext.forR4();
		log.trace("loading parser");
		parser = context.newXmlParser();
		log.trace("xml worker initialized");
	}

	public ERezept parseErezept(InputStream erezeptResource) {
		var bundle = parser.parseResource(Bundle.class, erezeptResource);
		return getERezeptDaten(bundle);
	}

	private ERezept getERezeptDaten(Bundle eRezeptBundle) {
		ERezept eRezept = new ERezept();

		for (BundleEntryComponent entry : eRezeptBundle.getEntry()) {
			String id = entry.getId();
			if (id == null) {
				if (entry.getLink().get(0).getUrl()
						.equals("https://fhir.kbv.de/StructureDefinition/KBV_PR_ERP_Bundle")) {
					id = ID_VERORDNUNGSDATEN;
				} else if (entry.getLink().get(0).getUrl()
						.equals("https://gematik.de/fhir/StructureDefinition/ErxReceipt")) {
					id = ID_QUITTUNGSDATEN;
				} else if (entry.getLink().get(0).getUrl().equals(
						"http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-PR-ERP-AbgabedatenBundle")) {
					id = ID_ABGABEDATEN;
				} else if (entry.getLink().get(0).getUrl()
						.equals("https://fhir.gkvsv.de/StructureDefinition/GKVSV_PR_ERP_eAbrechnungsdaten")) {
					id = ID_ABRECHNUNGSDATEN;
				}
			}
			switch (id) {
			case ID_VERORDNUNGSDATEN:
				Binary verordnungsBinary = (Binary) entry.getResource();
				Bundle verordnungsBundle = decodeBinaryToBundle(verordnungsBinary);
				var verordnungsdaten = getVerordnungsdaten(verordnungsBundle);
				fixVerordnungsdaten(verordnungsBundle, verordnungsdaten);
				eRezept.setVerordnungdaten(verordnungsdaten);
				log.debug("Got Verordnungsdaten");

				break;
			case ID_QUITTUNGSDATEN:
				Binary quittungsBinary = (Binary) entry.getResource();
				eRezept.setQuittungsdaten(getQuittungsdaten(quittungsBinary));
				log.debug("Got Quittungsdaten");
				break;
//        case ID_ABGABEDATEN:
//          Binary abgabeBinary = (Binary) entry.getResource();
//          Bundle abgabeBundle = decodeBinaryToBundle(abgabeBinary);
//          var zusatzdatenAbgabeDaten = getZusatzdatenAbgabedaten(abgabeBundle);
//          var combinedZusatzdaten =
//              getCombinedZusatzdaten(zusatzdatenAbrechnungsdaten, zusatzdatenAbgabeDaten);
//          eRezept.setAbgabedaten(getAbgabedaten(abgabeBundle));
//          eRezept.setZusatzdaten(combinedZusatzdaten);
//          log.debug("Got Abgabedaten");
//          break;
			case ID_ABRECHNUNGSDATEN:
				break;
			default:
				throw new RuntimeException(id + " ist unbekannt!");
			}
		}

		return eRezept;
	}

	/**
	 * This is a temporary solution that fixes some broken fields on the
	 * verordnungsdaten using FHIRPath All Verordnungsdaten fields should be
	 * extracted using FHIRPath in the future and this method should be removed
	 *
	 * @param verordnungsBundle
	 * @param verordnungsdaten
	 */
	private void fixVerordnungsdaten(Bundle verordnungsBundle, EVerordnungsdaten verordnungsdaten) {
		var medication = medicationExtraktor.getMedication(verordnungsBundle).orElseThrow();
		EVerordnungsdaten verordnungsdatenFromFhirPath = verordnungsdatenExtraktor.getVerordnungsdaten(medication);
		verordnungsdaten.setVRezeptur(verordnungsdatenFromFhirPath.getVRezeptur());
		verordnungsdaten.setVRezepturMenge(verordnungsdatenFromFhirPath.getVRezepturMenge());
		verordnungsdaten.setVRezepturEinheit(verordnungsdatenFromFhirPath.getVRezepturEinheit());
	}

	public EVerordnungsdaten getVerordnungsdaten(Bundle verordnungsBundle) {
		EVerordnungsdaten verordnungsdaten = new EVerordnungsdaten();

		if (verordnungsBundle != null) {
			if (verordnungsBundle != null && verordnungsBundle.hasIdentifier()
					&& verordnungsBundle.getIdentifier().hasValue()) {
				log.debug("Got erezeptid {}", verordnungsBundle.getIdentifier().getValue());
				String eRezID = verordnungsBundle.getIdentifier().getValue();
				verordnungsdaten.setRezeptId(eRezID);
			} else {
				log.warn("Please check erezeptid");
			}
			for (BundleEntryComponent e : verordnungsBundle.getEntry()) {
				if (e.hasResource()) {
					if (e.getResource().getResourceType() == ResourceType.Composition) {

						Composition composition = (Composition) e.getResource();
						log.debug("Got composition {}", composition.getId());

						if (composition.hasExtension()) {
							for (Extension extension : composition.getExtension()) {
								if (extension.hasValue() && (Coding) extension.getValue() instanceof Coding) {
									Coding valueCoding = (Coding) extension.getValue();
									if (valueCoding.hasCode()) {
										log.debug("Got rechts_kn {}", valueCoding.getCode());
										String rechtsKN = valueCoding.getCode();
										verordnungsdaten.setRechtsKn(rechtsKN);
									}
								}
							}
						}

					} else if (e.getResource().getResourceType() == ResourceType.Coverage) {
						Coverage coverage = (Coverage) e.getResource();
						log.debug("Got coverage {}", coverage.getId());
						List<Reference> payorList = coverage.getPayor();

						// CodeableConcept kostTrBGType = coverage.getType();
						// if (kostTrBGType == null) {
						// continue;
						// } else {
						// List<Coding> codingKostenTrBGList = kostTrBGType.getCoding();
						// for (Coding codingKostTrBG : codingKostenTrBGList) {
						// String strCode = codingKostTrBG.getCode();
						// verordnungsdaten.setKostentrBg(strCode);
						// log.debug(strCode);
						// }
						// }

						for (Reference ref : payorList) {
							log.debug("Got payorListValue kostentr_ik1 {}", ref.getIdentifier().getValue());
							String kostentrIk1Payor = ref.getIdentifier().getValue();
							if (checkStringWithRegex(kostentrIk1Payor)) {
								try {
									Integer kostentrIk1PayorInt = Integer.parseInt(kostentrIk1Payor);
									verordnungsdaten
											.setKostentrIk1(kostentrIk1PayorInt != null ? kostentrIk1PayorInt : 0);
								} catch (NumberFormatException ex) {
									log.debug(VALID_DIGIT_SEQUENCE, ex.getMessage());
								}
							} else {
								log.debug(NOT_ONLY_DIGITS);
							}

							boolean extRef = ref.getIdentifier()
									.hasExtension("https://fhir.kbv.de/StructureDefinition/KBV_EX_FOR_Alternative_IK");
							if (extRef) {
								Extension extValIdent = ref.getIdentifier().getExtensionByUrl(
										"https://fhir.kbv.de/StructureDefinition/KBV_EX_FOR_Alternative_IK");
								Identifier vIdentKostenTrBG = (Identifier) extValIdent.getValue();
								String strVIdentKostenTrBG = vIdentKostenTrBG.getValue();
								verordnungsdaten.setKostentrBg(strVIdentKostenTrBG);
								log.debug(strVIdentKostenTrBG);
							}

							String displayValue = ref.getDisplay();
							log.debug("Got payorListValue name Institut {}", displayValue);
							verordnungsdaten.setDisplayPayor(displayValue);
						}

						if (coverage.hasExtension("http://fhir.de/StructureDefinition/gkv/wop")) {
							Extension extWOP = coverage.getExtensionByUrl("http://fhir.de/StructureDefinition/gkv/wop");
							Coding valueCodeWOP = (Coding) extWOP.getValue();
							String wop = valueCodeWOP.getCode();
							verordnungsdaten.setVersWop(wop);
							log.debug("Got wop code {}", wop);
						}

						Extension extStatus = coverage
								.getExtensionByUrl("http://fhir.de/StructureDefinition/gkv/versichertenart");
						Coding valueCode = (Coding) extStatus.getValue();
						String versStatus = valueCode.getCode();
						log.debug("Got vers_status code {}", versStatus);
						verordnungsdaten.setVerstStatus(versStatus);

						Extension extBesPersonGruppe = coverage
								.getExtensionByUrl("http://fhir.de/StructureDefinition/gkv/besondere-personengruppe");
						Coding valueCodePersGruppe = (Coding) extBesPersonGruppe.getValue();
						String besPersoGrupp = valueCodePersGruppe.getCode();
						log.debug("Got bes_persongr code {}", besPersoGrupp);
						verordnungsdaten.setBesPersongr(besPersoGrupp);

						if (coverage.hasExtension(DMP_KENNZEICHEN)) {
							Extension extDMP = coverage.getExtensionByUrl(DMP_KENNZEICHEN);
							Coding valueDMP = (Coding) extDMP.getValue();
							String dmp = valueDMP.getCode();
							verordnungsdaten.setDmpStatus(dmp);
							log.debug("Got dmp_status code {}", dmp);
						}

					} else if (e.getResource().getResourceType() == ResourceType.Practitioner) {
						Practitioner practitioner = (Practitioner) e.getResource();
						log.debug("Got practitioner {}", practitioner.getId());

						PractitionerQualificationComponent codeArzt = practitioner.getQualificationFirstRep();
						if (codeArzt.getCode() == null) {
							continue;
						} else {

							CodeableConcept codConArzt = codeArzt.getCode();
							Coding code = codConArzt.getCodingFirstRep();
							String strCode = code.getCode();
							log.debug("Got codeArzt {}", strCode);
							verordnungsdaten.setCodeArzt(strCode);

							if (strCode.equals("03")) {
								Identifier arztNr = practitioner.getIdentifierFirstRep();
								String lanrStr = arztNr.getValue();
								log.debug("Got la_nr: verschreibende Person {}", lanrStr);
								log.debug("Got la_nr_v: verantwortliche Person {}", lanrStr);
								verordnungsdaten.setLaNrV(lanrStr);
							} else {
								Identifier arztNr = practitioner.getIdentifierFirstRep();
								String lanrStr = arztNr.getValue();
								log.debug("Got la_nr: verschreibende Person {}", lanrStr);
								log.debug("Got la_nr_v: verantwortliche Person {}", lanrStr);
								verordnungsdaten.setLaNr(lanrStr);
							}
						}

						// Alternative
						// Identifier identifierPR = practitioner.getIdentifier().get(0);
						// log.debug("Got LANR: {}", identifierPR.getValue());
						// log.debug("Got LANR_v {}", identifierPR.getValue());
						// verordnungsdaten.setLaNr(lanrStr);

					} else if (e.getResource().getResourceType() == ResourceType.PractitionerRole) {

						PractitionerRole practitionerRole = (PractitionerRole) e.getResource();
						log.debug("Got practitionerRole {}", practitionerRole.getId());

						if (practitionerRole != null) {
							String teamNr = practitionerRole.getOrganization().getIdentifier().getValue();
							log.debug("Got asv_nr {}", teamNr);
							verordnungsdaten.setAsvNr(teamNr);
						} // ASV_NR

					} else if (e.getResource().getResourceType() == ResourceType.Organization) {
						Organization organization = (Organization) e.getResource();
						log.debug("Got organization {}", organization.getId());
						if (organization.hasIdentifier()) {
							Identifier identifierOR = organization.getIdentifier().get(0);
							log.debug("Got BSNR: {}", identifierOR.getValue());
							String bsnr = identifierOR.getValue();
							verordnungsdaten.setBsNr(bsnr);
						}

						// String strASVNr = organization.getIdentifierFirstRep().getValue();
						// if (strASVNr == null) {
						// strASVNr = null;
						// verordnungsdaten.setAsvNr(strASVNr);
						// } else {
						// verordnungsdaten.setAsvNr(strASVNr);
						// }

					} else if (e.getResource().getResourceType() == ResourceType.Patient) {
						Patient patient = (Patient) e.getResource();
						log.debug("Got patient {}", patient.getId());
						Identifier identifierPA = patient.getIdentifier().get(0);
						String versNum = identifierPA.getValue();
						log.debug("Got pat_nr Vers.Num.: {}", versNum);
						verordnungsdaten.setPatNr(versNum);

						Date patGeboren = patient.getBirthDate();
						SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
						String vDateFormatted = df.format(patGeboren);
						log.debug("Got pat_geb birthDate.: {}", vDateFormatted);

						verordnungsdaten.setPatGeb(vDateFormatted != null ? vDateFormatted : "000000");

						List<HumanName> nameList = patient.getName();
						for (HumanName name : nameList) {
							String patVorName = name.getGivenAsSingleString();
							log.debug("Got pat_vorname: {}", patVorName);
							verordnungsdaten.setPatVorname(patVorName);

							String patFamName = name.getFamily();
							log.debug("Got pat_nachname: {}", patFamName);
							verordnungsdaten.setPatNachname(patFamName);
						}
					} else if (e.getResource().getResourceType() == ResourceType.MedicationRequest) {
						MedicationRequest medicationRequest = (MedicationRequest) e.getResource();
						log.debug("Got medicationRequest {}", medicationRequest.getId());
						if (medicationRequest.hasAuthoredOn()) {

							Date vDatum = medicationRequest.getAuthoredOn();
							log.debug("Got v_datum {}", vDatum);

							SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd"); // Korrektes Format
							String vDateFormatted = df.format(vDatum);

							if (vDateFormatted.length() < 8) {
								// Füge Nullen am Ende hinzu, bis es 8 Stellen hat
								while (vDateFormatted.length() < 8) {
									vDateFormatted += "0";
								}
							} else if (vDateFormatted.length() > 8) {
								// Schneide die letzten Stellen ab, bis es 8 Stellen hat
								vDateFormatted = vDateFormatted.substring(0, 8);
							}

							verordnungsdaten.setVDatum(vDateFormatted);
						}

						Extension extNoctu = medicationRequest.getExtensionByUrl(
								"https://fhir.kbv.de/StructureDefinition/KBV_EX_ERP_EmergencyServicesFee");
						String noctu = extNoctu.getValueAsPrimitive().getValueAsString() != null
								? extNoctu.getValueAsPrimitive().getValueAsString()
								: "";
						log.debug("Got noctu {}", noctu);

						Integer noctuInt;
						if (noctu.equals("false")) {
							noctuInt = 0;
							verordnungsdaten.setNoctu(noctuInt);
						} else if (noctu.equals("true")) {
							noctuInt = 1;
							verordnungsdaten.setNoctu(noctuInt);
						}

						if (medicationRequest.hasExtension(KBV_EX_FOR_ACCIDENT)) {
							Extension extUnfall = medicationRequest.getExtensionByUrl(KBV_EX_FOR_ACCIDENT);

							List<Extension> extLists = extUnfall.getExtension();
							if (extLists != null) {
								Extension extFirst = extLists.get(0);
								if (extFirst != null) {
									Coding codingFirst = (Coding) extFirst.getValue();
									if (codingFirst instanceof Coding) {

										String code = codingFirst.getCode().toString();
										verordnungsdaten.setUnfall(code);
									}

								} else {
									continue;
								}

							} else {
								continue;
							}
						}

						Extension extBVG = medicationRequest
								.getExtensionByUrl("https://fhir.kbv.de/StructureDefinition/KBV_EX_ERP_BVG");
						String bBVG = extBVG.getValueAsPrimitive().getValueAsString();
						log.debug("Got bvg {}", bBVG);
						Integer result = (bBVG != null && Boolean.parseBoolean(bBVG)) ? 1 : 0;
						try {
							verordnungsdaten.setBvg(result);
						} catch (NumberFormatException ex) {
							log.debug(VALID_DIGIT_SEQUENCE, ex.getMessage());
						}

						if (medicationRequest.hasExtension(
								"https://fhir.kbv.de/StructureDefinition/KBV_EX_ERP_Multiple_Prescription")) {

							Extension extKennz = medicationRequest.getExtensionByUrl(
									"https://fhir.kbv.de/StructureDefinition/KBV_EX_ERP_Multiple_Prescription");
							Extension extKennSub = extKennz.getExtensionByUrl("Kennzeichen");
							String mehrfachKn = extKennSub.getValueAsPrimitive().getValueAsString() != null
									? extKennSub.getValueAsPrimitive().getValueAsString()
									: "";
							log.debug("Got mehrfach_kn {}", mehrfachKn);

							Integer mehrfachKnInt;
							if (mehrfachKn.equals("false")) {
								mehrfachKnInt = 0;
								verordnungsdaten.setMehrfachKn(mehrfachKnInt);
							} else if (mehrfachKn.equals("true")) {
								mehrfachKnInt = 1;
								verordnungsdaten.setMehrfachKn(mehrfachKnInt);

								try {
									String numerator;
									String denominator;
									Ratio numRatio = (Ratio) extKennz.getExtensionByUrl("Nummerierung").getValue();
									numerator = numRatio.getNumerator().getValue().toString();
									verordnungsdaten.setMehrfachZ(numerator);
									denominator = numRatio.getDenominator().getValue().toString();
									verordnungsdaten.setMehrfachN(denominator);
								} catch (Exception ex) {
									log.error("Denominator or Nominator " + ex.getMessage());
								}

								try {
									// Zeitraum -> start(muss),end (kann)
									LocalDate startLD = null;
									LocalDate endLD = null;
									java.util.Date start = null;
									java.util.Date end = null;
									if (extKennSub != null) {
										try {
											Period period = (Period) extKennz.getExtensionByUrl("Zeitraum").getValue();
											try {
												start = period.getStart();
												startLD = start.toInstant().atZone(ZoneId.systemDefault())
														.toLocalDate();
												DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
												String formattedDate = startLD.format(formatter);
												verordnungsdaten.setMehrfachStart(formattedDate);

											} catch (Exception exc) {
												log.debug("Mehrfach_start ");
											}
											try {
												end = period.getEnd();
												endLD = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

												DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
												String formattedDate = endLD.format(formatter);
												verordnungsdaten.setMehrfachEnde(formattedDate);
											} catch (Exception exc) {
												log.debug("Mehrfach_ende ");
											}
										} catch (Exception ex) {
											log.debug("Mehrfachdates ");
										}
									}

								} catch (Exception ex) {
									log.error("Mehrfach_start && || Mehrfach_ende" + ex.getMessage());
								}
							}
						}
						MedicationRequestSubstitutionComponent medSub = medicationRequest.getSubstitution();
						if (medSub == null) {
							continue;
						} else {
							Type substitutionType = medSub.getAllowed();
							if (substitutionType == null) {
								continue;
							} else {
								String autIdem = substitutionType.primitiveValue() != null
										? substitutionType.primitiveValue()
										: "";
								log.debug("Got substitution aut_idem {}", autIdem);

								Integer autIdemInt;
								if (autIdem.equals("false")) {
									autIdemInt = 0;
									verordnungsdaten.setAutIdem(autIdemInt);
								} else if (autIdem.equals("true")) {
									autIdemInt = 1;
									verordnungsdaten.setAutIdem(autIdemInt);
								}
							}
						}

						if (medicationRequest.hasExtension(KBV_EX_FOR_STATUSCOPAYMENT)) {
							Extension extGebBefr = medicationRequest.getExtensionByUrl(KBV_EX_FOR_STATUSCOPAYMENT);
							Coding valuegebBefr = (Coding) extGebBefr.getValue();
							String gebBefr = valuegebBefr.getCode();
							if (checkStringWithRegex(gebBefr)) {
								try {
									Integer strGebBefrInt = Integer.parseInt(gebBefr);
									verordnungsdaten.setGebBefr(strGebBefrInt);
									log.debug("Got geb_befr code {}", strGebBefrInt);
								} catch (NumberFormatException ex) {
									log.debug(VALID_DIGIT_SEQUENCE, ex.getMessage());
								}
							} else {
								log.debug(NOT_ONLY_DIGITS);
							}
						}

						List<Dosage> dos = medicationRequest.getDosageInstruction();
						for (Dosage dosage : dos) {
							Extension extDosKn = dosage
									.getExtensionByUrl("https://fhir.kbv.de/StructureDefinition/KBV_EX_ERP_DosageFlag");
							if (extDosKn == null) {
								continue;
							} else {

								String dosierungKN = extDosKn.getValue().primitiveValue().replaceAll(";", " ");
								log.debug("Got dosierung_kn value {} ", dosierungKN);
								if (dosierungKN.equals("true")) {
									verordnungsdaten.setDosierungKn("1");
								} else if (dosierungKN.equals("false")) {
									verordnungsdaten.setDosierungKn("0");
								} else {
									verordnungsdaten.setDosierungKn("null");
								}
							}
						}

						Dosage dosOrg = medicationRequest.getDosageInstructionFirstRep();
						String dosierung = dosOrg.getText();
						if (dosierung == null) {
							verordnungsdaten.setDosierung("");
						} else {
							String cleanedDosierung = dosierung.replaceAll("[^\\p{ASCII}]", "").replace("<", "")
									.replace(">", "");
							log.debug("Got dosierung value {} ", dosierung);

							verordnungsdaten.setDosierung(cleanedDosierung);
						}

						MedicationRequestDispenseRequestComponent medReqDis = medicationRequest.getDispenseRequest();
						Quantity quanReqDis = medReqDis.getQuantity();
						String vAnzPackungen = quanReqDis.getValue().toString();
						log.debug("Got v_anz_packungen code {} ", vAnzPackungen);
						verordnungsdaten.setVAnzPackungen(vAnzPackungen);
					} else if (e.getResource().getResourceType() == ResourceType.Medication) {
						Medication medication = (Medication) e.getResource();
						log.debug("Got medication {}", medication.getId());

						if (medication.hasExtension(KBV_EX_ERP_MEDICATION_CATEGORY)) {
							Extension extVKat = medication.getExtensionByUrl(KBV_EX_ERP_MEDICATION_CATEGORY);

							Coding valueVKat = (Coding) extVKat.getValue();
							String vKat = valueVKat.getCode();
							verordnungsdaten.setVKategorie(vKat);
							log.debug("Got v_kategorie code {}", vKat);
						}

						Extension extImpf = medication.getExtensionByUrl(
								"https://fhir.kbv.de/StructureDefinition/KBV_EX_ERP_Medication_Vaccine");
						String impf = extImpf.getValueAsPrimitive().getValueAsString();
						log.debug("Got impf {}", impf);
						if ("false".equals(impf)) {
							verordnungsdaten.setImpf(0);
						} else if ("true".equals(impf)) {
							verordnungsdaten.setImpf(1);
						} else {
							log.debug("Got impf bitte prüfen");
						}

						// boolean vRezMeta = medication.hasMeta();
						// if(vRezMeta) {
						// Meta metaVRez = medication.getMeta();
						// boolean profileMetaVrez = metaVRez.hasProfile();
						// if(profileMetaVrez) {
						// List<CanonicalType> profileList = metaVRez.getProfile();
						// for(CanonicalType canProfiles : profileList) {
						// canProfiles.getValue();
						// }
						// }
						// }

						// VREZ Attribute nicht sauber
						CodeableConcept codRez = medication.getCode();
						List<Coding> codList = codRez.getCoding();
						for (Coding cods : codList) {
							String sysCod = cods.getSystem();
							log.debug("Got vRezeptur sycode {}", sysCod);
							if (!sysCod.contains("ifa")) {
								log.debug(sysCod);
								String vRezeptur = codRez.getText();
								log.debug("Got vRezeptur v_rezeptur {}", vRezeptur);
								verordnungsdaten.setVRezeptur(vRezeptur);
							}

							Ratio ratRezMenge = medication.getAmount();
							Quantity numRezMenge = ratRezMenge.getNumerator();
							List<Extension> extrezMengeList = numRezMenge.getExtension();
							for (Extension extrezMenge : extrezMengeList) {
								Type vRezepturMenge = extrezMenge.getValue();
								String strVRezMenge = vRezepturMenge.toString();
								log.debug("Got v_rezeptur_menge v_rezeptur_menge {}", vRezepturMenge);
								verordnungsdaten.setVRezepturMenge(strVRezMenge);
							}

							Ratio ratvRezEinh = medication.getAmount();
							Quantity ratvRezEinhNum = ratvRezEinh.getNumerator();
							String vRezepturEinheit = ratvRezEinhNum.getUnit();
							log.debug("Got v_rezeptur_einheit vRezepturEinheit {}", vRezepturEinheit);
							verordnungsdaten.setVRezepturEinheit(vRezepturEinheit);
						}

						Ratio ratVEinh = medication.getAmount();
						Quantity quantVEinh = ratVEinh.getNumerator();
						String vEinheit = quantVEinh.getUnit();
						log.debug("Got vEinheit v_einheit {}", vEinheit);
						verordnungsdaten.setVEinheit(vEinheit);

						List<CanonicalType> medPorfList = medication.getMeta().getProfile();
						for (CanonicalType medProf : medPorfList) {

							String valMedProf = medProf.getValue();
							log.debug(valMedProf);
						}

						CodeableConcept x = medication.getForm();
						List<Coding> xList = x.getCoding();
						for (Coding codVDF : xList) {
							String codeVDFStr = codVDF.getCode();
							log.debug("Got v_df code {}", codeVDFStr);
							verordnungsdaten.setVDf(codeVDFStr);
						}

						if (medication.hasExtension("http://fhir.de/StructureDefinition/normgroesse")) {
							Extension extNormGr = medication
									.getExtensionByUrl("http://fhir.de/StructureDefinition/normgroesse");
							Type normGrType = extNormGr.getValue();

							if (normGrType.toString() == null) {
								verordnungsdaten.setVNormgroesse("");
							} else {
								verordnungsdaten.setVNormgroesse(normGrType.toString());
							}

							log.debug("Got extNormGr {}", normGrType);
						}

						Ratio rat = medication.getAmount();
						Quantity quantDen = rat.getDenominator();
						DecimalType decType = quantDen.getValueElement();
						String denominator = decType.getValueAsString();
						log.debug("Got denominator {}", denominator);
						Quantity quantNum = rat.getNumerator();
						Extension extFisrtQuantNum = quantNum.getExtensionFirstRep();
						Type typeextFisrtQuantNum = extFisrtQuantNum.getValue();
						if (typeextFisrtQuantNum == null) {
							verordnungsdaten.setVStaerke("");
						} else {
							String strStaerke = typeextFisrtQuantNum.toString();
							verordnungsdaten.setVStaerke(strStaerke);
							log.debug("Got v_staerke numerator {}", strStaerke);
						}

						List<MedicationIngredientComponent> wirkstoffList = medication.getIngredient();
						for (MedicationIngredientComponent ingredientComponent : wirkstoffList) {
							CodeableConcept codeConIngr = ingredientComponent.getItemCodeableConcept();
							List<Coding> codingList = codeConIngr.getCoding();
							for (Coding coding : codingList) {
								String strCode = coding.getCode();
								log.debug("Got v_wikstoffnr {}", strCode);
								verordnungsdaten.setVWirkstoffnr(strCode);
							}

							String textIngredient = codeConIngr.getText();
							log.debug("Got v_wikstoff {}", textIngredient);
							verordnungsdaten.setVWirkstoff(textIngredient);

							Ratio strength = ingredientComponent.getStrength();
							Quantity numStrength = strength.getNumerator();
							BigDecimal v_wirkstaerke = numStrength.getValue();
							log.debug("Got v_wirkstaerke {}", v_wirkstaerke);
							verordnungsdaten.setVWirkstaerke(v_wirkstaerke.toString());

							String v_wirkstaerkeeinheit = numStrength.getUnit();
							log.debug("Got v_wirkstaerkeeinheit {}", v_wirkstaerkeeinheit);
							verordnungsdaten.setVWirkstaerkeEinheit(v_wirkstaerkeeinheit);
						}

						CodeableConcept codConPZN = medication.getCode();
						List<Coding> codListPZN = codConPZN.getCoding();
						for (Coding cods : codListPZN) {
							String vPZN = cods.getCode();

							if (vPZN.matches("\\d+")) {

								String mutantString = padLeftZeros(vPZN);
								verordnungsdaten.setVPzn(mutantString);
							} else {
								verordnungsdaten.setVPzn("");
							}
						}
					}
				}
			}
		}
		return verordnungsdaten;
	}

	private EQuittungsdaten getQuittungsdaten(Binary quittungsBinary) {
		EQuittungsdaten quittungsdaten = new EQuittungsdaten();

		Bundle quittungsBundle = decodeUnsignedBinaryToBundle(quittungsBinary);
		if (quittungsBundle != null) {
			for (BundleEntryComponent quitBundleEntry : quittungsBundle.getEntry()) {
				if (quitBundleEntry.hasResource()
						&& quitBundleEntry.getResource().getResourceType() == ResourceType.Composition) {
					Composition composition = (Composition) quitBundleEntry.getResource();
					Date qDatum = composition.getDate();
					if (qDatum == null) {
						continue;
					} else {
						log.debug("Got q_datum {}", qDatum.toString());
						SimpleDateFormat df = new SimpleDateFormat("yyyyMMDD");
						String qDateFormatted = df.format(qDatum);
						log.debug("Got quittungsDatum q_datum {}", qDateFormatted);
						quittungsdaten.setQDatum(qDateFormatted);
					}
				}
			}
		}
		return quittungsdaten;
	}

	private EAbgabedaten getAbgabedaten(Bundle abgabeBundle) {
		EAbgabedaten abgabedaten = new EAbgabedaten();

		if (abgabeBundle != null) {
			for (BundleEntryComponent e : abgabeBundle.getEntry()) {
				if (e.hasResource()) {
					if (e.getResource().getResourceType() == ResourceType.MedicationDispense) {
						MedicationDispense medicationDispense = (MedicationDispense) e.getResource();
						log.debug("Got medicationDispense {}", medicationDispense.getId());

						// http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-EX-ERP-ZusatzdatenHerstellung

						if (medicationDispense.hasWhenPrepared()) {
							Date whenP = medicationDispense.getWhenPrepared();
							if (whenP == null) {
								continue;
							} else {

								String date = whenP.toString();
								log.debug(date);
								// Verarbeitung
								final String OLD_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";
								final String NEW_FORMAT = "yyyyMMdd:HHmm";

								try {
									SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT, Locale.ENGLISH);
									Date d = sdf.parse(date);

									sdf.applyPattern(NEW_FORMAT);
									String newDateString = sdf.format(d);

									abgabedaten.setHDatum(newDateString);
									log.debug("Umgewandeltes Datum: " + newDateString);
								} catch (ParseException ex) {
									System.err.println("Fehler beim Parsen des Datums: " + ex.getMessage());
								}
							}
						}

						if (medicationDispense.hasWhenHandedOver()) {
							Date whenHandedOver = medicationDispense.getWhenHandedOver();
							log.debug("Got a_datum {}", whenHandedOver.toString());

							SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd"); // Korrektes Format
							String aDateFormatted = df.format(whenHandedOver); // Verwende das Date-Objekt direkt

							if (aDateFormatted.length() < 8) {
								// Füge Nullen am Ende hinzu, bis es 8 Stellen hat
								while (aDateFormatted.length() < 8) {
									aDateFormatted += "0";
								}
							} else if (aDateFormatted.length() > 8) {
								// Schneide die letzten Stellen ab, bis es 8 Stellen hat
								aDateFormatted = aDateFormatted.substring(0, 8);
							} // TODO: Arrays zu jeweiligen Datensätzen und dann schreiben

							abgabedaten.setADatum(aDateFormatted);
						}

						MedicationDispenseSubstitutionComponent substMedRZ = medicationDispense.getSubstitution();
						if (!substMedRZ.hasExtension(
								"http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-EX-ERP-Rezeptaenderung")) {
							continue;
						} else {

							Extension extSubst = substMedRZ.getExtensionFirstRep();
							if (!extSubst.hasExtension("ArtRezeptaenderung")) {
								continue;
							} else {
								Extension extArtRezAend = extSubst.getExtensionByUrl("ArtRezeptaenderung");

								CodeableConcept codRezAend = (CodeableConcept) extArtRezAend.getValue();
								List<Coding> codingRezAendList = codRezAend.getCoding();
								for (Coding cod : codingRezAendList) {
									String strCod = cod.getCode();
									log.debug("Got rz_aend  {}", strCod);
									abgabedaten.setRzAend(strCod);
								}
							}
						}

						if (medicationDispense.hasExtension(DAV_EX_ERP_VERTAGSKENNZEICHEN)) {
							Extension extValStr = medicationDispense.getExtensionByUrl(DAV_EX_ERP_VERTAGSKENNZEICHEN);
							IPrimitiveType<?> primVal = extValStr.getValueAsPrimitive();
							String primValStr = primVal.toString();
							log.debug("Vertragskennzeichen: {}", primValStr);
							abgabedaten.setVertragKn(primValStr);
						}

					} else if (e.getResource().getResourceType() == ResourceType.Invoice) {
						Invoice invoice = (Invoice) e.getResource();
						log.debug("Got invoice {}", invoice.getId());

						if (invoice.hasExtension(DAV_EX_ERP_ZAEHLER)) {
							Extension extPosVal = invoice.getExtensionByUrl(DAV_EX_ERP_ZAEHLER);
							Type typePosVal = extPosVal.getValue();
							log.debug("positiveIntegerValue: {}", typePosVal);
							abgabedaten.setHZaehler(typePosVal.toString());
						}

						if (invoice.hasExtension(
								"http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-EX-ERP-ZusatzdatenHerstellung")) {
							Extension zeahlerEinheitExt = invoice.getExtensionByUrl(
									"http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-EX-ERP-ZusatzdatenHerstellung");
							zeahlerEinheitExt.getValue();
						}

						List<InvoiceLineItemComponent> lineItemList11 = invoice.getLineItem();

						List<String> rMengeList = new ArrayList<>();

						List<String> rVKList = new ArrayList<>();

						List<String> vkKNList = new ArrayList<>();

						for (InvoiceLineItemComponent lineItem : lineItemList11) {

							int sequence = lineItem.getSequence();
							String strSequence = String.valueOf(sequence);
							log.debug("Got sequence zaehler_abr_zeile {}", strSequence);
							abgabedaten.setZaehlerAbrZeile(strSequence);

							List<InvoiceLineItemPriceComponentComponent> priceComList = lineItem.getPriceComponent();
							for (InvoiceLineItemPriceComponentComponent invLineItemPriceCom : priceComList) {
								Meta metaData = invoice.getMeta();
								List<CanonicalType> metaProfileList = metaData.getProfile();
								for (CanonicalType metaProfile : metaProfileList) {
									String valProflie = metaProfile.getValue();
									if (valProflie.contains("Abrechnungszeilen")) {
										log.debug("GOT Menge");
										// Zuletzt bearbeitet
										BigDecimal mengeFromFactor = invLineItemPriceCom.getFactor();
										String strMenge = mengeFromFactor.toString();
										log.debug("Got menge  {}", strMenge);
										abgabedaten.setMenge(strMenge);
									}
								}

								Money amount = invLineItemPriceCom.getAmount();
								if (amount != null) {

									String bigDAmount = amount.getValue().toString().replace(".", "");
									rVKList.add(bigDAmount);

									abgabedaten.setRVkList(rVKList);
									abgabedaten.setRVk(rVKList.toString());

									log.debug("Got r_vk {}", rVKList.toString());
								}

								if (invLineItemPriceCom.hasExtension(
										"http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-EX-ERP-ZusatzdatenPreiskennzeichen")) {
									Extension extPriceCom = invLineItemPriceCom.getExtensionByUrl(
											"http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-EX-ERP-ZusatzdatenPreiskennzeichen");
									CodeableConcept priceComCode = (CodeableConcept) extPriceCom.getValue();
									List<Coding> codingListPriceCom = priceComCode.getCoding();
									for (Coding codingPriceCom : codingListPriceCom) {
										String code = codingPriceCom.getCode();
										vkKNList.add(code);
										log.debug("Got preiskennzeichen {}", vkKNList.toString());
										abgabedaten.setVkKnList(vkKNList);
										abgabedaten.setVkKn(vkKNList.toString());
									}
								}

								BigDecimal y = invLineItemPriceCom.getFactor();
								String strY = y.toString();
								rMengeList.add(strY);
								abgabedaten.setRMengeList(rMengeList);
								abgabedaten.setRMenge(rMengeList.toString()); // eventuell noch nicht sicher PUSH FAILED

								if (invLineItemPriceCom.hasExtension(
										"http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-EX-ERP-ZusatzdatenFaktorkennzeichen")) {
									Extension x = invLineItemPriceCom.getExtensionByUrl(
											"http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-EX-ERP-ZusatzdatenFaktorkennzeichen");
									CodeableConcept codMengeKN = (CodeableConcept) x.getValue();
									List<Coding> codingMengeKn = codMengeKN.getCoding();
									for (Coding cod : codingMengeKn) {
										String strCod = cod.getCode();
										log.debug("Got menge_kn  {}", strCod);
										abgabedaten.setMengeKn(strCod);
									}
								}
								Money amountVal = invLineItemPriceCom.getAmount();
								BigDecimal decAmountVal = amountVal.getValue();
								String amountBV = decAmountVal.toString().replace(".", "");
								log.debug("Got apo_vo betragvalue {}", amount);

								if (checkStringWithRegex(amountBV)) {
									try {
										Integer amountInt = Integer.parseInt(amountBV);
										abgabedaten.setApoVo(amountInt != null ? amountInt : 0);
									} catch (NumberFormatException ex) {
										log.debug(VALID_DIGIT_SEQUENCE, ex.getMessage());
									}
								} else {
									log.debug(NOT_ONLY_DIGITS);
								}
							}

							List<Extension> lienExts = lineItem.getExtension();
							for (Extension exts : lienExts) {
								if (exts.getUrl().contains("Zusatzattribute")) {
									List<Extension> ersatzVextList = exts.getExtension();

									for (Extension ersatzVext : ersatzVextList) {
										if (ersatzVext.getUrl().contains("Ersatz")) {

											Extension schluesselExt = ersatzVext.getExtensionByUrl("Schluessel");
											Type cvalBool = schluesselExt.getValue();
											String strValBol = cvalBool.toString();
											log.debug("Got s_ersatzvo -> schluessel {}", strValBol);

											Extension schluesselExtGruppe = ersatzVext.getExtensionByUrl("Gruppe");
											CodeableConcept valSchluesselExt = (CodeableConcept) schluesselExtGruppe
													.getValue();
											List<Coding> codingSchluessel = valSchluesselExt.getCoding();
											for (Coding codingPriceCom : codingSchluessel) {
												String code = codingPriceCom.getCode();
												log.debug("Got s_ersatzvo -> gruppe {}", code);
												abgabedaten.setSErsatzvo("1");
											}
										}
										if (ersatzVext.getUrl().contains("Wirkstoffver")) {

											Extension schluesselExt = ersatzVext.getExtensionByUrl("Schluessel");
											Type cvalBool = schluesselExt.getValue();
											String strValBol = cvalBool.toString();
											log.debug("Got s_ersatzvo -> schluessel {}", strValBol);

											Extension schluesselExtGruppe = ersatzVext.getExtensionByUrl("Gruppe");
											CodeableConcept valSchluesselExt = (CodeableConcept) schluesselExtGruppe
													.getValue();
											List<Coding> codingSchluessel = valSchluesselExt.getCoding();
											for (Coding codingPriceCom : codingSchluessel) {
												String code = codingPriceCom.getCode();
												log.debug("Got s_ersatzvo -> gruppe {}", code);
												abgabedaten.setSWirkstoffvo("1");
											}
										}
										if (ersatzVext.getUrl().contains("Wunscharznei")) {

											Extension schluesselExt = ersatzVext.getExtensionByUrl("Schluessel");
											Type cvalBool = schluesselExt.getValue();
											String strValBol = cvalBool.toString();
											log.debug("Got s_ersatzvo -> schluessel {}", strValBol);

											Extension schluesselExtGruppe = ersatzVext.getExtensionByUrl("Gruppe");
											CodeableConcept valSchluesselExt = (CodeableConcept) schluesselExtGruppe
													.getValue();
											List<Coding> codingSchluessel = valSchluesselExt.getCoding();
											for (Coding codingPriceCom : codingSchluessel) {
												String code = codingPriceCom.getCode();
												log.debug("Got s_ersatzvo -> gruppe {}", code);
												abgabedaten.setSWunschAm("1");
											}
										} //
										if (ersatzVext.getUrl().contains("tMehrkostenueb")) {

											Extension schluesselExt = ersatzVext.getExtensionByUrl("Schluessel");
											Type cvalBool = schluesselExt.getValue();
											String strValBol = cvalBool.toString();
											log.debug("Got s_ersatzvo -> schluessel {}", strValBol);

											Extension schluesselExtGruppe = ersatzVext.getExtensionByUrl("Gruppe");
											CodeableConcept valSchluesselExt = (CodeableConcept) schluesselExtGruppe
													.getValue();
											List<Coding> codingSchluessel = valSchluesselExt.getCoding();
											for (Coding codingPriceCom : codingSchluessel) {
												String code = codingPriceCom.getCode();
												log.debug("Got s_ersatzvo -> gruppe {}", code);
												abgabedaten.setSMehrkostenUebern("1");
											}
										} //
										if (ersatzVext.getUrl().contains("tEinzelimportierte")) {

											Extension schluesselExt = ersatzVext.getExtensionByUrl("Schluessel");
											Type cvalBool = schluesselExt.getValue();
											String strValBol = cvalBool.toString();
											log.debug("Got s_ersatzvo -> schluessel {}", strValBol);

											Extension schluesselExtGruppe = ersatzVext.getExtensionByUrl("Gruppe");
											CodeableConcept valSchluesselExt = (CodeableConcept) schluesselExtGruppe
													.getValue();
											List<Coding> codingSchluessel = valSchluesselExt.getCoding();
											for (Coding codingPriceCom : codingSchluessel) {
												String code = codingPriceCom.getCode();
												log.debug("Got s_ersatzvo -> gruppe {}", code);
												abgabedaten.setSEImpFam("1");
											}
										} //
										if (ersatzVext.getUrl().contains("AbgabeNoctu")) {

											Extension schluesselExt = ersatzVext.getExtensionByUrl("Schluessel");
											Type cvalBool = schluesselExt.getValue();
											String strValBol = cvalBool.toString();
											log.debug("Got s_ersatzvo -> schluessel {}", strValBol);

											Extension schluesselExtGruppe = ersatzVext.getExtensionByUrl("Gruppe");
											CodeableConcept valSchluesselExt = (CodeableConcept) schluesselExtGruppe
													.getValue();
											List<Coding> codingSchluessel = valSchluesselExt.getCoding();
											for (Coding codingPriceCom : codingSchluessel) {
												String code = codingPriceCom.getCode();
												log.debug("Got s_ersatzvo -> gruppe {}", code);
												abgabedaten.setSNotdienst("1");
											}
										} //
										if (ersatzVext.getUrl().contains("tZuzahlungsstatus")) {

											Extension schluesselExt = ersatzVext.getExtensionByUrl("Schluessel");
											Type cvalBool = schluesselExt.getValue();
											String strValBol = cvalBool.toString();
											log.debug("Got s_ersatzvo -> schluessel {}", strValBol);

											Extension schluesselExtGruppe = ersatzVext.getExtensionByUrl("Gruppe");
											CodeableConcept valSchluesselExt = (CodeableConcept) schluesselExtGruppe
													.getValue();
											List<Coding> codingSchluessel = valSchluesselExt.getCoding();
											for (Coding codingPriceCom : codingSchluessel) {
												String code = codingPriceCom.getCode();
												log.debug("Got s_ersatzvo -> gruppe {}", code);
												abgabedaten.setSZuzahlungsStatus("1");
											}
										}
									}
								}
							}
						}

						for (InvoiceLineItemComponent lineItems : lineItemList11) {

							if (lineItems.hasExtension(
									"http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-EX-ERP-Chargenbezeichnung")) {
								Extension extCharg = lineItems.getExtensionFirstRep();
								Type x = extCharg.getValue();
								if (x == null) {
									continue;
								} else {
									log.debug("Got chargem_bez: {}", x.toString());
									abgabedaten.setChargenBez(x.toString());
								}
							}

							List<InvoiceLineItemPriceComponentComponent> priceComList = lineItems.getPriceComponent();

							for (InvoiceLineItemPriceComponentComponent priceComs : priceComList) {

								List<Extension> extKatList = priceComs.getExtension();
								for (Extension extKat : extKatList) {

									if (!extKat.hasExtension("Kategorie")) {
										continue;
									} else {
										Extension kategorie = extKat.getExtensionByUrl("Kategorie");
										CodeableConcept codCon = (CodeableConcept) kategorie.getValue();
										List<Coding> codList = codCon.getCoding();
										for (Coding codeKat : codList) {
											String code = codeKat.getCode();
											log.debug(code);
											if (code.equals("0")) {
												Extension kostenBetrag = extKat.getExtensionByUrl("Kostenbetrag");
												Money mon = (Money) kostenBetrag.getValue();
												BigDecimal zuzahlung = mon.getValue();
												String strZuzahlung = zuzahlung.toString().replace(".", "");
												log.debug("Got zuzahlung k_vers_zuz {}", strZuzahlung);
												abgabedaten.setKVersZuz(Integer.parseInt(strZuzahlung));
											} else if (code.equals("1")) {
												Extension kostenBetrag = extKat.getExtensionByUrl("Kostenbetrag");
												Money mon = (Money) kostenBetrag.getValue();
												BigDecimal zuzahlung = mon.getValue();
												String strZuzahlung = zuzahlung.toString().replace(".", "");
												log.debug("Got zuzahlung k_vers_zuz {}", strZuzahlung);
												abgabedaten.setKVersMehrk(Integer.parseInt(strZuzahlung));
											} else if (code.equals("2")) {
												Extension kostenBetrag = extKat.getExtensionByUrl("Kostenbetrag");
												Money mon = (Money) kostenBetrag.getValue();
												BigDecimal zuzahlung = mon.getValue();
												String strZuzahlung = zuzahlung.toString().replace(".", "");
												log.debug("Got zuzahlung k_vers_zuz {}", strZuzahlung);
												abgabedaten.setKVersEigenb(Integer.parseInt(strZuzahlung));
											}
										}
									}
								}
							}
						}

						CodeableConcept type = invoice.getType();

						List<Coding> codTypeList = type.getCoding();

						for (Coding codType : codTypeList) {
							String codingType = codType.getCode();

							if (codingType.contains("ZusatzdatenEinheit")) {
								List<InvoiceLineItemComponent> lineItemsList = invoice.getLineItem();
								if (invoice.hasExtension(
										"http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-EX-ERP-Zaehler")) {
									Extension posInt = invoice.getExtensionByUrl(
											"http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-EX-ERP-Zaehler");
									IPrimitiveType<?> posIntPrimType = posInt.getValueAsPrimitive();
									String xStr = posIntPrimType.getValueAsString();
									if (xStr == null) {
										continue;
									} else {

										log.debug(xStr);
										abgabedaten.setHZaehler(xStr);
									}
								}
							}
						}

						List<InvoiceLineItemComponent> lineItemMengeList = invoice.getLineItem();

						List<String> rPznList = new ArrayList<>();

						for (InvoiceLineItemComponent lineItemMenge : lineItemMengeList) {

							List<InvoiceLineItemPriceComponentComponent> lineItemMengePriceComList = lineItemMenge
									.getPriceComponent();

							CodeableConcept chargeItem = lineItemMenge.getChargeItemCodeableConcept();
							List<Coding> chargeItemCodList = chargeItem.getCoding();
							for (Coding cod : chargeItemCodList) {
								String codeStr = cod.getCode();
								rPznList.add(codeStr);
								log.debug("Got r_pzn: {}", codeStr);
								abgabedaten.setRPznList(rPznList);
								abgabedaten.setRPzn(rPznList.toString());
							}
						}

						Meta meta = invoice.getMeta();
						List<CanonicalType> profileList = meta.getProfile();

						for (CanonicalType canProfile : profileList) {
							String profileValue = canProfile.getValue();
							log.debug("Got profileValue: {}", profileValue);

							List<InvoiceLineItemComponent> lineItemList = invoice.getLineItem();
							if (profileValue.contains(
									"http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-PR-ERP-Abrechnungszeilen")) {

								for (InvoiceLineItemComponent invLineItem : lineItemList) {
									CodeableConcept chargeItem = invLineItem.getChargeItemCodeableConcept();
									List<Coding> codingCharegItemList = chargeItem.getCoding();
									for (Coding cod : codingCharegItemList) {
										String codeStr = cod.getCode();
										log.debug("Got pzn: {}", codeStr);
										abgabedaten.setPzn(codeStr);
									}
								}
							}

							// List<String> invList = new ArrayList<>();
							//
							// for (InvoiceLineItemComponent invLineItem : lineItemList) {
							// int intinv = invLineItem.getSequence();
							// String strInv = String.valueOf(intinv);
							// invList.add(strInv);
							// log.debug("Got zaehler_abr_pos: {}", intinv);
							// abgabedaten.setZaehlerAbrPos(invList.toString());
							// } Before

						}

						if (invoice.hasExtension(
								"http://fhir.gkvsv.de/StructureDefinition/GKVSV_EX_ERP_ZusatzdatenHerstellung")) {
							Extension extZusDatHerst = invoice.getExtensionByUrl(
									"http://fhir.gkvsv.de/StructureDefinition/GKVSV_EX_ERP_ZusatzdatenHerstellung");
							if (extZusDatHerst.hasExtension("einheit")) {
								Extension extEinheit = extZusDatHerst.getExtensionByUrl("einheit");
								if (extEinheit.hasExtension("zaehlerEinheit")) {
									Extension extZaehlerEinh = extEinheit.getExtensionByUrl("zaehlerEinheit");
									extZaehlerEinh.getValue();
									log.debug(DAV_EX_ERP_VERTAGSKENNZEICHEN);
									abgabedaten.setZaehlerEinheit(DAV_EX_ERP_VERTAGSKENNZEICHEN); // TODO
																									// zaehler_einheit
																									// valposint
								}
							}
						}

						if (invoice.hasExtension(
								"http://fhir.gkvsv.de/StructureDefinition/GKVSV_EX_ERP_ZusatzdatenHerstellung")) {
							Extension extZusDatHerst = invoice.getExtensionByUrl(
									"http://fhir.gkvsv.de/StructureDefinition/GKVSV_EX_ERP_ZusatzdatenHerstellung");
							if (extZusDatHerst.hasExtension("einheit")) {
								Extension extEinheit = extZusDatHerst.getExtensionByUrl("einheit");
								if (extEinheit.hasExtension("abrechnungsPosition")) {
									Extension extAbrechnPos = extEinheit.getExtensionByUrl("abrechnungsPosition");
									if (extAbrechnPos.hasExtension("zaehlerAbrechnungsposotion")) {
										Extension extZaehlerAbrechnPos = extAbrechnPos
												.getExtensionByUrl("zaehlerAbrechnungsposotion");
										extZaehlerAbrechnPos.getValue();
										log.debug(DAV_EX_ERP_VERTAGSKENNZEICHEN);
										abgabedaten.setZaehlerEinheit(DAV_EX_ERP_VERTAGSKENNZEICHEN); // TODO
																										// zaehler_abr_pos
																										// valposint
									}
								}
							}
						}

						Money totalGross = invoice.getTotalGross();

						if (!totalGross.hasValue()) {
							continue;
						} else {
							BigDecimal monVal = totalGross.getValue();
							String strMonVal = monVal.toString().replace(".", "");
							log.debug("Got money g_avp_vo {}", strMonVal);
							if (checkStringWithRegex(strMonVal)) {
								try {
									Integer strMonValInt = Integer.parseInt(strMonVal);
									abgabedaten.setGAvpVo(strMonValInt != null ? strMonValInt : 0);
								} catch (NumberFormatException ex) {
									log.debug(VALID_DIGIT_SEQUENCE, ex.getMessage());
								}
							} else {
								log.debug(NOT_ONLY_DIGITS);
							}
						}

						List<Extension> tGvals = totalGross.getExtensionsByUrl(
								"http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-EX-ERP-Gesamtzuzahlung");
						for (Extension ext : tGvals) {

							Money mon = (Money) ext.getValue();
							BigDecimal zuzahlung = mon.getValue();
							String strZuzahlung = zuzahlung.toString().replace(".", "");
							log.debug("Got zuzahlung g_zuz_vo {}", strZuzahlung);
							if (checkStringWithRegex(strZuzahlung)) {
								try {
									Integer strZuzahlungInt = Integer.parseInt(strZuzahlung);
									abgabedaten.setGZuzVo(strZuzahlungInt != null ? strZuzahlungInt : 0);
								} catch (NumberFormatException ex) {
									log.debug(VALID_DIGIT_SEQUENCE, ex.getMessage());
								}
							} else {
								log.debug(NOT_ONLY_DIGITS);
							}

							String currency = mon.getCurrency();
							log.debug("Got currency {}", currency);
						}

						List<InvoiceLineItemComponent> invLineItemComList = invoice.getLineItem();

						// InvoiceLineItemComponent cx = invLineItemComList.get(0);
						// List<Extension> extCXList = cx.getExtension();
						// log.debug(DAV_EX_ERP_VERTAGSKENNZEICHEN); // http: //
						// //
						// fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-EX-ERP-Zusatzattribute
						// if (extCXList != null) {
						//
						// Extension kuenstl = extCXList.get(0);
						// Extension extKuenstl =
						// kuenstl.getExtensionByUrl("ZusatzattributKuenstlicheBefruchtung");
						// if (extKuenstl == null) {
						// continue;
						// } else {
						//
						// Extension extKuenstlGruppe = extKuenstl.getExtensionByUrl("Gruppe");
						//
						// CodeableConcept valGrupExtKuenst = (CodeableConcept)
						// extKuenstlGruppe.getValue();
						// List<Coding> codingListGrupKuenstList = valGrupExtKuenst.getCoding();
						// for (Coding codingPriceCom : codingListGrupKuenstList) {
						// String code = codingPriceCom.getCode();
						// log.debug("Got s_kuenstl_bef -> gruppe {}", code);
						// abgabedaten.setSKuenstlBef("1");
						// }
						// }
						// }

						// InvoiceLineItemComponent cx = invLineItemComList.get(0);
						// List<Extension> extCXList = cx.getExtension();
						// log.debug(
						// DAV_EX_ERP_VERTAGSKENNZEICHEN); //
						//
						// http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-EX-ERP-Zusatzattribute
						// if (extCXList.size() <= 0) {
						// continue;
						// } else {
						// Extension kuenstl = extCXList.get(0);
						// Extension extKuenstl =
						// kuenstl.getExtensionByUrl("ZusatzattributKuenstlicheBefruchtung");
						// if (extKuenstl == null) {
						// continue;
						// } else {
						//
						// Extension extKuenstlGruppe = extKuenstl.getExtensionByUrl("Gruppe");
						//
						// CodeableConcept valGrupExtKuenst = (CodeableConcept)
						// extKuenstlGruppe.getValue();
						// List<Coding> codingListGrupKuenstList = valGrupExtKuenst.getCoding();
						// for (Coding codingPriceCom : codingListGrupKuenstList) {
						// String code = codingPriceCom.getCode();
						// log.debug("Got s_kuenstl_bef -> gruppe {}", code);
						// abgabedaten.setSKuenstlBef("1");
						// }
						// }
						// }

						for (InvoiceLineItemComponent invLineItemCom : invLineItemComList) {

							if (invLineItemCom.hasExtension(
									"http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-EX-ERP-Zusatzattribute")) {
								Extension x = invLineItemCom.getExtensionByUrl(
										"http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-EX-ERP-Zusatzattribute");

								if (x.hasExtension("ZusatzattributFAM")) {
									Extension zusFam = x.getExtensionByUrl("ZusatzattributFAM");

									List<Extension> extensionsList = zusFam.getExtension();

									for (Extension extFromS : extensionsList) {

										if (extFromS.getUrl().equals("Markt")) {
											log.debug(extFromS.getUrl());
											Extension grupExt = extFromS.getExtensionByUrl("Gruppe");

											CodeableConcept valGrupExt = (CodeableConcept) grupExt.getValue();
											List<Coding> codingListGrupList = valGrupExt.getCoding();
											for (Coding codingPriceCom : codingListGrupList) {
												String code = codingPriceCom.getCode();
												log.debug("Got s_markt -> gruppe {}", code);
											}

											Extension schluesselExt = extFromS.getExtensionByUrl("Schluessel");

											CodeableConcept valSchluesselExt = (CodeableConcept) schluesselExt
													.getValue();
											List<Coding> codingSchluessel = valSchluesselExt.getCoding();
											for (Coding codingPriceCom : codingSchluessel) {
												String code = codingPriceCom.getCode();
												log.debug("Got s_markt -> schluessel {}", code);
												abgabedaten.setSMarkt(Integer.parseInt(code));
											}

										} else if (extFromS.getUrl().equals("Rabattvertragserfuellung")) {
											log.debug(extFromS.getUrl());

											Extension grupExt = extFromS.getExtensionByUrl("Gruppe");

											CodeableConcept valGrupExt = (CodeableConcept) grupExt.getValue();
											List<Coding> codingListGrupList = valGrupExt.getCoding();
											for (Coding codingPriceCom : codingListGrupList) {
												String code = codingPriceCom.getCode();
												log.debug("Got s_rabatt -> gruppe {}", code);
											}

											Extension schluesselExt = extFromS.getExtensionByUrl("Schluessel");

											CodeableConcept valSchluesselExt = (CodeableConcept) schluesselExt
													.getValue();
											List<Coding> codingSchluessel = valSchluesselExt.getCoding();
											for (Coding codingPriceCom : codingSchluessel) {
												String code = codingPriceCom.getCode();
												log.debug("Got s_rabatt -> schluessel {}", code);
												abgabedaten.setSRabatt(Integer.parseInt(code));
											}
										} else if (extFromS.getUrl().equals("PreisguenstigesFAM")) {
											log.debug(extFromS.getUrl());
											Extension grupExt = extFromS.getExtensionByUrl("Gruppe");

											CodeableConcept valGrupExt = (CodeableConcept) grupExt.getValue();
											List<Coding> codingListGrupList = valGrupExt.getCoding();
											for (Coding codingPriceCom : codingListGrupList) {
												String code = codingPriceCom.getCode();
												log.debug("Got s_preisg -> gruppe {}", code);
											}

											Extension schluesselExt = extFromS.getExtensionByUrl("Schluessel");

											CodeableConcept valSchluesselExt = (CodeableConcept) schluesselExt
													.getValue();
											List<Coding> codingSchluessel = valSchluesselExt.getCoding();
											for (Coding codingPriceCom : codingSchluessel) {
												String code = codingPriceCom.getCode();
												log.debug("Got s_preisg -> schluessel {}", code);
												abgabedaten.setSPreisg(Integer.parseInt(code));
											}
										} else if (extFromS.getUrl().equals("ImportFAM")) {
											log.debug(extFromS.getUrl());
											Extension grupExt = extFromS.getExtensionByUrl("Gruppe");

											CodeableConcept valGrupExt = (CodeableConcept) grupExt.getValue();
											List<Coding> codingListGrupList = valGrupExt.getCoding();
											for (Coding codingPriceCom : codingListGrupList) {
												String code = codingPriceCom.getCode();
												log.debug("Got setSImportFam -> gruppe {}", code);
											}

											Extension schluesselExt = extFromS.getExtensionByUrl("Schluessel");

											CodeableConcept valSchluesselExt = (CodeableConcept) schluesselExt
													.getValue();
											List<Coding> codingSchluessel = valSchluesselExt.getCoding();
											for (Coding codingPriceCom : codingSchluessel) {
												String code = codingPriceCom.getCode();
												log.debug("Got setSImportFam -> schluessel {}", code);
												abgabedaten.setSImportFam(code);
											}
										} else if (extFromS.getUrl().equals("ZusatzattributMehrkostenuebernahme")) {
											log.debug(extFromS.getUrl());
											Extension grupExt = extFromS.getExtensionByUrl("Gruppe");

											CodeableConcept valGrupExt = (CodeableConcept) grupExt.getValue();
											List<Coding> codingListGrupList = valGrupExt.getCoding();
											for (Coding codingPriceCom : codingListGrupList) {
												String code = codingPriceCom.getCode();
												log.debug("Got mehrkosten_uebern -> gruppe {}", code);
											}

											Extension schluesselExt = extFromS.getExtensionByUrl("Schluessel");

											CodeableConcept valSchluesselExt = (CodeableConcept) schluesselExt
													.getValue();
											List<Coding> codingSchluessel = valSchluesselExt.getCoding();
											for (Coding codingPriceCom : codingSchluessel) {
												String code = codingPriceCom.getCode();
												log.debug("Got mehrkosten_uebern -> schluessel {}", code);
												abgabedaten.setSMehrkostenUebern(code);
											}
										} else if (extFromS.getUrl().equals("ZusatzattributWunscharzneimittel")) {
											log.debug(extFromS.getUrl());
											Extension grupExt = extFromS.getExtensionByUrl("Gruppe");

											CodeableConcept valGrupExt = (CodeableConcept) grupExt.getValue();
											List<Coding> codingListGrupList = valGrupExt.getCoding();
											for (Coding codingPriceCom : codingListGrupList) {
												String code = codingPriceCom.getCode();
												log.debug("Got mehrkosten_uebern -> gruppe {}", code);
											}

											Extension schluesselExt = extFromS.getExtensionByUrl("Schluessel");

											CodeableConcept valSchluesselExt = (CodeableConcept) schluesselExt
													.getValue();
											List<Coding> codingSchluessel = valSchluesselExt.getCoding();
											for (Coding codingPriceCom : codingSchluessel) {
												String code = codingPriceCom.getCode();
												log.debug("Got s_wunsch_am -> schluessel {}", code);
												abgabedaten.setSWunschAm(code);
											}
										} else if (extFromS.getUrl().equals("ZusatzattributWirkstoffverordnung")) {
											log.debug(extFromS.getUrl());
											Extension grupExt = extFromS.getExtensionByUrl("Gruppe");

											CodeableConcept valGrupExt = (CodeableConcept) grupExt.getValue();
											List<Coding> codingListGrupList = valGrupExt.getCoding();
											for (Coding codingPriceCom : codingListGrupList) {
												String code = codingPriceCom.getCode();
												log.debug("Got mehrkosten_uebern -> gruppe {}", code);
											}

											Extension schluesselExt = extFromS.getExtensionByUrl("Schluessel");

											CodeableConcept valSchluesselExt = (CodeableConcept) schluesselExt
													.getValue();
											List<Coding> codingSchluessel = valSchluesselExt.getCoding();
											for (Coding codingPriceCom : codingSchluessel) {
												String code = codingPriceCom.getCode();
												log.debug("Got s_wirkstoffvo -> schluessel {}", code);
												abgabedaten.setSWirkstoffvo(code);
											}
										} else if (extFromS.getUrl().equals("ZusatzattributErsatzverordnung")) {
											log.debug(extFromS.getUrl());
											Extension grupExt = extFromS.getExtensionByUrl("Gruppe");

											CodeableConcept valGrupExt = (CodeableConcept) grupExt.getValue();
											List<Coding> codingListGrupList = valGrupExt.getCoding();
											for (Coding codingPriceCom : codingListGrupList) {
												String code = codingPriceCom.getCode();
												log.debug("Got mehrkosten_uebern -> gruppe {}", code);
											}

											Extension schluesselExt = extFromS.getExtensionByUrl("Schluessel");

											CodeableConcept valSchluesselExt = (CodeableConcept) schluesselExt
													.getValue();
											List<Coding> codingSchluessel = valSchluesselExt.getCoding();
											for (Coding codingPriceCom : codingSchluessel) {
												String code = codingPriceCom.getCode();
												log.debug("Got s_ersatzvo -> schluessel {}", code);
												abgabedaten.setSErsatzvo(code);
											}
										} else if (extFromS.getUrl().equals("ZusatzattributEinzelimportierteFAM")) {
											log.debug(extFromS.getUrl());
											Extension grupExt = extFromS.getExtensionByUrl("Gruppe");

											CodeableConcept valGrupExt = (CodeableConcept) grupExt.getValue();
											List<Coding> codingListGrupList = valGrupExt.getCoding();
											for (Coding codingPriceCom : codingListGrupList) {
												String code = codingPriceCom.getCode();
												log.debug("Got mehrkosten_uebern -> gruppe {}", code);
											}

											Extension schluesselExt = extFromS.getExtensionByUrl("Schluessel");

											CodeableConcept valSchluesselExt = (CodeableConcept) schluesselExt
													.getValue();
											List<Coding> codingSchluessel = valSchluesselExt.getCoding();
											for (Coding codingPriceCom : codingSchluessel) {
												String code = codingPriceCom.getCode();
												log.debug("Got s_e_imp_fam -> schluessel {}", code);
												abgabedaten.setSEImpFam(code);
											}
										} else if (extFromS.getUrl().equals("ZusatzattributAbgabeNoctu")) {
											log.debug(extFromS.getUrl());
											Extension grupExt = extFromS.getExtensionByUrl("Gruppe");

											CodeableConcept valGrupExt = (CodeableConcept) grupExt.getValue();
											List<Coding> codingListGrupList = valGrupExt.getCoding();
											for (Coding codingPriceCom : codingListGrupList) {
												String code = codingPriceCom.getCode();
												log.debug("Got mehrkosten_uebern -> gruppe {}", code);
											}

											Extension schluesselExt = extFromS.getExtensionByUrl("Schluessel");

											CodeableConcept valSchluesselExt = (CodeableConcept) schluesselExt
													.getValue();
											List<Coding> codingSchluessel = valSchluesselExt.getCoding();
											for (Coding codingPriceCom : codingSchluessel) {
												String code = codingPriceCom.getCode();
												log.debug("Got s_notdiesnt -> schluessel {}", code);
												abgabedaten.setSNotdienst(code);
											}
										} else if (extFromS.getUrl().equals("ZusatzattributZuzahlungsstatus")) {
											log.debug(extFromS.getUrl());
											Extension grupExt = extFromS.getExtensionByUrl("Gruppe");

											CodeableConcept valGrupExt = (CodeableConcept) grupExt.getValue();
											List<Coding> codingListGrupList = valGrupExt.getCoding();
											for (Coding codingPriceCom : codingListGrupList) {
												String code = codingPriceCom.getCode();
												log.debug("Got mehrkosten_uebern -> gruppe {}", code);
											}

											Extension schluesselExt = extFromS.getExtensionByUrl("Schluessel");

											CodeableConcept valSchluesselExt = (CodeableConcept) schluesselExt
													.getValue();
											List<Coding> codingSchluessel = valSchluesselExt.getCoding();
											for (Coding codingPriceCom : codingSchluessel) {
												String code = codingPriceCom.getCode();
												log.debug("Got s_zuzahlungsstatus -> schluessel {}", code);
												abgabedaten.setSZuzahlungsStatus(code);
											}
										}
									}
								}

								if (x.hasExtension("ZusatzattributKuenstlicheBefruchtung")) {
									Extension kuenstlBef = x.getExtensionByUrl("ZusatzattributKuenstlicheBefruchtung");

									log.debug(kuenstlBef.getUrl());
									Extension grupExt = kuenstlBef.getExtensionByUrl("Gruppe");

									CodeableConcept valGrupExt = (CodeableConcept) grupExt.getValue();
									List<Coding> codingListGrupList = valGrupExt.getCoding();
									for (Coding codingPriceCom : codingListGrupList) {
										String code = codingPriceCom.getCode();
										log.debug("Got s_kuenstl_bef -> gruppe {}", code);
										abgabedaten.setSKuenstlBef("1");
									}
								}
							}

							List<InvoiceLineItemPriceComponentComponent> priceComList = invLineItemCom
									.getPriceComponent();
							for (InvoiceLineItemPriceComponentComponent iLPCC : priceComList) {

								List<Extension> extList = iLPCC.getExtension();
								Extension extNodeOne = extList.get(1);
								if (!extNodeOne.hasExtension("Kategorie")) {
									continue;
								} else {
									List<Extension> nodeList = extNodeOne.getExtension();
									for (Extension nodes : nodeList) {
										log.debug("nodes");
										log.debug("" + nodes.getValue());
										if (!nodes.getUrl().equals("Kategorie")) {
											continue;
										} else {

											CodeableConcept codNodeType = (CodeableConcept) nodes.getValue();
											List<Coding> codeList = codNodeType.getCoding();

											for (Coding cod : codeList) {
												log.debug("" + cod.getCode());

												if (cod.getCode().equals("0")) {
													log.debug("0");
													Extension extUrlKostenBetrag = extNodeOne
															.getExtensionByUrl("Kostenbetrag");
													if (extUrlKostenBetrag == null) {
														continue;
													} else {
														Money kostenBetrag = (Money) extUrlKostenBetrag.getValue();

														BigDecimal bKostenBetrag = kostenBetrag.getValue();
														if (bKostenBetrag != null) {
															String getKostenBetrag = bKostenBetrag.toString()
																	.replace(".", "");
															if (checkStringWithRegex(getKostenBetrag)) {
																try {
																	Integer gKostenBetragInt = Integer
																			.parseInt(getKostenBetrag);
																	abgabedaten.setGZuzVo(
																			gKostenBetragInt != null ? gKostenBetragInt
																					: 0);
																	log.debug("Got Kostenbetrag k_vers_zuz {}",
																			abgabedaten.getGZuzVo());
																	abgabedaten.setKVersZuz(gKostenBetragInt);
																} catch (NumberFormatException ex) {
																	log.debug(VALID_DIGIT_SEQUENCE, ex.getMessage());
																}
															} else {
																log.debug(NOT_ONLY_DIGITS);
															}
														}
													}
												} else if (cod.getCode().equals("1")) {
													log.debug("1");
													Extension extUrlKostenBetrag = extNodeOne
															.getExtensionByUrl("Kostenbetrag");
													if (extUrlKostenBetrag == null) {
														continue;
													} else {
														Money kostenBetrag = (Money) extUrlKostenBetrag.getValue();

														BigDecimal bKostenBetrag = kostenBetrag.getValue();
														if (bKostenBetrag != null) {
															String getKostenBetrag = bKostenBetrag.toString()
																	.replace(".", "");
															if (checkStringWithRegex(getKostenBetrag)) {
																try {
																	Integer gKostenBetragInt = Integer
																			.parseInt(getKostenBetrag);
																	abgabedaten.setGZuzVo(
																			gKostenBetragInt != null ? gKostenBetragInt
																					: 0);
																	log.debug("Got Kostenbetrag k_vers_mehrk {}",
																			abgabedaten.getKVersMehrk());
																	abgabedaten.setKVersMehrk(gKostenBetragInt);
																} catch (NumberFormatException ex) {
																	log.debug(VALID_DIGIT_SEQUENCE, ex.getMessage());
																}
															} else {
																log.debug(NOT_ONLY_DIGITS);
															}
														}
													}
												} else if (cod.getCode().equals("2")) {
													log.debug("2");
													Extension extUrlKostenBetrag = extNodeOne
															.getExtensionByUrl("Kostenbetrag");
													if (extUrlKostenBetrag == null) {
														continue;
													} else {
														Money kostenBetrag = (Money) extUrlKostenBetrag.getValue();

														BigDecimal bKostenBetrag = kostenBetrag.getValue();
														if (bKostenBetrag != null) {
															String getKostenBetrag = bKostenBetrag.toString()
																	.replace(".", "");
															if (checkStringWithRegex(getKostenBetrag)) {
																try {
																	Integer gKostenBetragInt = Integer
																			.parseInt(getKostenBetrag);
																	abgabedaten.setGZuzVo(
																			gKostenBetragInt != null ? gKostenBetragInt
																					: 0);
																	log.debug("Got Kostenbetrag k_vers_eigenb {}",
																			abgabedaten.getKVersEigenb());
																	abgabedaten.setKVersEigenb(gKostenBetragInt);
																} catch (NumberFormatException ex) {
																	log.debug(VALID_DIGIT_SEQUENCE, ex.getMessage());
																}
															} else {
																log.debug(NOT_ONLY_DIGITS);
															}
														}
													}
												}
											}
										}
									}
								}

								Extension totalGrossUrl = totalGross.getExtensionByUrl(
										"http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-EX-ERP-Gesamtzuzahlung");
								if (totalGrossUrl == null) {
									continue;
								} else {
									Money gZuzVoMon = (Money) totalGrossUrl.getValue();
									if (gZuzVoMon == null) {

									} else {
										BigDecimal bigDgZuzVo = gZuzVoMon.getValue();
										String getZuz = bigDgZuzVo.toString().replace(".", "");

										if (checkStringWithRegex(getZuz)) {
											try {
												Integer gZuzInt = Integer.parseInt(getZuz);
												abgabedaten.setGZuzVo(gZuzInt != null ? gZuzInt : 0);
												log.debug("Got g_zuz_vo {}", abgabedaten.getGZuzVo());
											} catch (NumberFormatException ex) {
												log.debug(VALID_DIGIT_SEQUENCE, ex.getMessage());
											}
										} else {
											log.debug(NOT_ONLY_DIGITS);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return abgabedaten;
	}

	// ---------------------------------- Helper Functions
	// ----------------------------------

	private Bundle parseToBundle(InputStream is) {
		return parser.parseResource(Bundle.class, is);
	}

	private int getValueFor(Extension extension) {
		Identifier valueIdentifier = (Identifier) extension.getValue();
		return Integer.parseInt(valueIdentifier.getValue());
	}

	private Bundle decodeUnsignedBinaryToBundle(Binary binary) {

		String bundleString = parseCMS(binary);

		if (!bundleString.isBlank()) {
			log.trace(bundleString);
			return context.newXmlParser().parseResource(Bundle.class, bundleString);
		}

		return null;
	}

	private Bundle decodeBinaryToBundle(Binary binary) {
		try {
			Base64BinaryType data = binary.getDataElement();
			byte[] decoded = Base64.getDecoder().decode(data.asStringValue());
			byte[] plainData = getUnsignedFhirData(decoded);
			log.trace(new String(plainData));
			ByteArrayInputStream bias = new ByteArrayInputStream(plainData);

			return (Bundle) parser.parseResource(bias);
		} catch (CMSException e) {
			e.printStackTrace();
		}

		return null;
	}

	private byte[] getUnsignedFhirData(byte[] signedFhirData) throws CMSException {
		CMSSignedData cmsSignedData = new CMSSignedData(signedFhirData);
		CMSProcessable cmsProcessable = cmsSignedData.getSignedContent();

		return (byte[]) cmsProcessable.getContent();
	}

	public boolean checkStringWithRegex(String a) {
		return a.matches("\\d+");
	}

	public static int countVerordnungOccurrences(String filePath) {
		int count = 0;
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			Scanner scanner = new Scanner(fis);

			String targetWord = ID_VERORDNUNGSDATEN;
			Pattern pattern = Pattern.compile("\\b" + targetWord + "\\b", Pattern.CASE_INSENSITIVE);

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				Matcher matcher = pattern.matcher(line);
				while (matcher.find()) {
					count++;
				}
			}
			scanner.close();
			fis.close();
		} catch (IOException e) {
			log.debug("Fehler beim Lesen der XML-Datei: {}", e.getMessage());
		}
		return count;
	}

	public static String padLeftZeros(String inputString) {

		int length;
		if (inputString.length() > 8) {
			length = 10;
		} else {
			length = 8;
		}

		StringBuilder sb = new StringBuilder();
		while (sb.length() < length - inputString.length()) {
			sb.append('0');
		}
		sb.append(inputString);

		return sb.toString();
	}

	public String parseCMS(Binary binary) {
		byte[] pkcs7Data = binary.getData();
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
}
