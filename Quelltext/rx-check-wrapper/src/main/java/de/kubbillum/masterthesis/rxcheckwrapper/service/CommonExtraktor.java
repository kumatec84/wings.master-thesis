package de.kubbillum.masterthesis.rxcheckwrapper.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.fhirpath.IFhirPath;
import java.math.BigDecimal;
import lombok.Getter;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.StringType;

@Getter
public class CommonExtraktor {

  protected IFhirPath path;

  public CommonExtraktor() {
    FhirContext context = FhirContext.forR4();
    path = context.newFhirPath();
  }

  public String evaluateFirstStringOrEmpty(Base input, IFhirPath.IParsedExpression expression) {
    return path.evaluateFirst(input, expression, StringType.class)
        .map(StringType::toString)
        .orElse("");
  }

  public int evaluateFirstIntegerFromString(Base input, IFhirPath.IParsedExpression expression) {
    return path.evaluateFirst(input, expression, StringType.class)
        .map(text -> Integer.valueOf(text.getValue()))
        .orElseThrow();
  }

  public String evaluateFirstCentAmount(Base input, IFhirPath.IParsedExpression expression) {
    return path.evaluateFirst(input, expression, DecimalType.class)
        .map(value -> value.getValue().multiply(BigDecimal.valueOf(100)).toBigInteger().toString())
        .orElse("");
  }

  public String evaluateFirstCentAmountFromString(
      Base input, IFhirPath.IParsedExpression expression) {
    return path.evaluateFirst(input, expression, StringType.class)
        .map(
            value ->
                new BigDecimal(value.getValue())
                    .multiply(BigDecimal.valueOf(100))
                    .toBigInteger()
                    .toString())
        .orElse("");
  }
}
