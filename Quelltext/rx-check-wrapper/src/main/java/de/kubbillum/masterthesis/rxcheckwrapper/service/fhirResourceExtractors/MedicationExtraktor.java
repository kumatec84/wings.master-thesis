package de.kubbillum.masterthesis.rxcheckwrapper.service.fhirResourceExtractors;

import ca.uhn.fhir.fhirpath.IFhirPath;
import java.util.Optional;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Medication;

public class MedicationExtraktor {
  private final IFhirPath.IParsedExpression medicationPath;

  private final CommonExtraktor commonExtraktor;

  public MedicationExtraktor(CommonExtraktor commonExtraktor) {
    this.commonExtraktor = commonExtraktor;
    try {
      this.medicationPath = this.commonExtraktor.path.parse(buildMedicationPath());
    } catch (Exception e) {
      throw new RuntimeException("Could not parse expression: " + buildMedicationPath());
    }
  }

  public Optional<Medication> getMedication(Bundle bundle) {
    return commonExtraktor.getPath().evaluateFirst(bundle, medicationPath, Medication.class);
  }

  private String buildMedicationPath() {
    return "Bundle.entry.resource.ofType(Medication)";
  }
}
