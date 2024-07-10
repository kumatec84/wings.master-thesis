package de.kubbillum.masterthesis.rxcheckwrapper.service.fhirResourceExtractors;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.fhirpath.IFhirPath;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import lombok.Getter;
import org.hl7.fhir.r4.model.*;

@Getter
public class CommonExtraktor {

  private final SimpleDateFormat zidataDateFormat = new SimpleDateFormat("yyyyMMdd");
  private final DecimalFormat decimalFormat = new DecimalFormat("0.######");

  public IFhirPath path;

  public CommonExtraktor() {
    FhirContext context = FhirContext.forR4();
    path = context.newFhirPath();
  }

  public String evaluateFirstStringOrEmpty(Base input, IFhirPath.IParsedExpression expression) {
    return path.evaluateFirst(input, expression, StringType.class)
        .map(StringType::toString)
        .orElse("");
  }

  public String evaluateFirstStringFromPositiveIntOrEmpty(
      Base input, IFhirPath.IParsedExpression expression) {
    return path.evaluateFirst(input, expression, PositiveIntType.class)
        .map(positiveIntType -> positiveIntType.getValue().toString())
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
        .map(value -> formatBigDecimalAsCent(new BigDecimal(value.getValue())))
        .orElse("");
  }

  public String evaluateDateTimeToZidataDate(Base input, IFhirPath.IParsedExpression expression) {
    return path.evaluateFirst(input, expression, DateTimeType.class)
        .map(value -> zidataDateFormat.format(value.getValue()))
        .orElseThrow();
  }

  public String formatBigDecimalMaxSixDigits(BigDecimal value) {
    return decimalFormat.format(value);
  }

  public String formatBigDecimalAsCent(BigDecimal value) {
    return value.multiply(BigDecimal.valueOf(100)).toBigInteger().toString();
  }
}
