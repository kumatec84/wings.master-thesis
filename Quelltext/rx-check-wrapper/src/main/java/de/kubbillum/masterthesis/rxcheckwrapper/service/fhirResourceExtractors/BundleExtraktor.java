package de.kubbillum.masterthesis.rxcheckwrapper.service.fhirResourceExtractors;

import ca.uhn.fhir.fhirpath.IFhirPath;
import java.util.Optional;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Invoice;

public class BundleExtraktor {

  private final IFhirPath.IParsedExpression erezeptBundleToInvoice;

  private final CommonExtraktor commonExtraktor;

  public BundleExtraktor(CommonExtraktor commonExtraktor) {
    this.commonExtraktor = commonExtraktor;
    try {
      this.erezeptBundleToInvoice =
          this.commonExtraktor.path.parse(buildInvoiceFromErezeptBundlePath());
    } catch (Exception e) {
      throw new RuntimeException(
          "Could not parse expression: " + buildInvoiceFromErezeptBundlePath());
    }
  }

  public Optional<Invoice> getTa7Invoice(Bundle bundle) {
    return commonExtraktor.getPath().evaluateFirst(bundle, erezeptBundleToInvoice, Invoice.class);
  }

  private String buildInvoiceFromErezeptBundlePath() {
    return "Bundle.entry.resource.ofType(Invoice)";
  }
}
