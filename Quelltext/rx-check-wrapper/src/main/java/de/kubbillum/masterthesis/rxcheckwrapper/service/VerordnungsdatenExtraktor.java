package de.kubbillum.masterthesis.rxcheckwrapper.service;

import ca.uhn.fhir.fhirpath.IFhirPath;
import de.kubbillum.masterthesis.rxcheckwrapper.model.EVerordnungsdaten;

import org.hl7.fhir.r4.model.Medication;

public class VerordnungsdatenExtraktor {
  private static final String MEDICATION_COMPOUNDING_PROFILE =
      "https://fhir.kbv.de/StructureDefinition/KBV_PR_ERP_Medication_Compounding|1.1.0";

  private static final String MEDICATION_COMPOUNDING_BASE_PATH =
      String.format("Medication.where(meta.profile='%s')", MEDICATION_COMPOUNDING_PROFILE);

  private final CommonExtraktor commonExtraktor;
  private final IFhirPath.IParsedExpression rezepturName;
  private final IFhirPath.IParsedExpression rezepturMenge;
  private final IFhirPath.IParsedExpression rezepturEinheit;

  public VerordnungsdatenExtraktor(CommonExtraktor commonExtraktor) {
    this.commonExtraktor = commonExtraktor;

    try {
      this.rezepturName = commonExtraktor.getPath().parse(buildRezepturQuery());
      this.rezepturMenge = commonExtraktor.getPath().parse(buildRezepturMengeQuery());
      this.rezepturEinheit = commonExtraktor.getPath().parse(buildRezepturEinheitQuery());

    } catch (Exception e) {
      throw new RuntimeException("Could not parse expression");
    }
  }

  public EVerordnungsdaten getVerordnungsdaten(Medication medication) {
    EVerordnungsdaten abgabedaten = new EVerordnungsdaten();
    setRezeptur(abgabedaten, medication);
    return abgabedaten;
  }

  private void setRezeptur(EVerordnungsdaten abgabedaten, Medication medication) {
    abgabedaten.setVRezeptur(
        this.commonExtraktor.evaluateFirstStringOrEmpty(medication, rezepturName));
    abgabedaten.setVRezepturMenge(
        this.commonExtraktor.evaluateFirstStringOrEmpty(medication, rezepturMenge));
    abgabedaten.setVRezepturEinheit(
        this.commonExtraktor.evaluateFirstStringOrEmpty(medication, rezepturEinheit));
  }

  private String buildRezepturQuery() {
    return String.format("%s.code.text", MEDICATION_COMPOUNDING_BASE_PATH);
  }

  private String buildRezepturMengeQuery() {
    return String.format("%s.amount.numerator.extension.value", MEDICATION_COMPOUNDING_BASE_PATH);
  }

  private String buildRezepturEinheitQuery() {
    return String.format("%s.amount.numerator.unit", MEDICATION_COMPOUNDING_BASE_PATH);
  }
}
