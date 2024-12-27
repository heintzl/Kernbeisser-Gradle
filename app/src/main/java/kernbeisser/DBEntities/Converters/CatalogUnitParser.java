package kernbeisser.DBEntities.Converters;

import static java.util.regex.Pattern.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Useful.Tools;
import lombok.Getter;

public class CatalogUnitParser {

  private final String source;
  @Getter private MetricUnits metricUnits;
  @Getter private Double amount;

  public CatalogUnitParser(String source) {
    this.source = source;
    parseSource();
  }

  private Double parseDouble(String strDbl) {
    try {
      return Double.parseDouble(strDbl.replace(",", "."));
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private void parseSource() {
    if (source.isBlank()) {
      metricUnits = MetricUnits.NONE;
      return;
    }
    Pattern pattern =
        Pattern.compile(
            "^(?<decAmount1>\\d*[,.]?\\d*)\\s*(?<multiplier>x?)\\s*(?<decAmount2>\\d*[,.]?\\d*)\\s*(?<unit>\\w*)$",
            CASE_INSENSITIVE + UNICODE_CHARACTER_CLASS + UNICODE_CASE);
    Matcher matcher = pattern.matcher(source);
    int amountFactor = 1;
    if (matcher.find()) {
      String decAmount1 = matcher.group("decAmount1");
      String multiplier = matcher.group("multiplier");
      String decAmount2 = matcher.group("decAmount2");
      String unit = matcher.group("unit");
      switch (unit) {
        case "g" -> {
          metricUnits = MetricUnits.GRAM;
        }
        case "mg" -> {
          metricUnits = MetricUnits.MILLIGRAM;
        }
        case "kg" -> {
          metricUnits = MetricUnits.GRAM;
          amountFactor = 1000;
        }
        case "ml" -> {
          metricUnits = MetricUnits.MILLILITER;
        }
        case "l" -> {
          metricUnits = MetricUnits.MILLILITER;
          amountFactor = 1000;
        }
        default -> {
          metricUnits = MetricUnits.PIECE;
        }
      }
      if (!decAmount1.isBlank()) {
        amount = parseDouble(decAmount1);
        if (multiplier.equalsIgnoreCase("x") && decAmount2.isBlank()) {
          amount *= Tools.ifNull(parseDouble(decAmount2), 1.0);
        }
      } else if (decAmount2.isBlank()) {
        amount = parseDouble(decAmount2);
      }
      if (amount != null) {
        amount *= amountFactor;
      }
    }
  }
}
