package kernbeisser.Tasks.Catalog;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import kernbeisser.DBEntities.CatalogDataSource;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.VAT;
import kernbeisser.Exeptions.InvalidValue;
import kernbeisser.Exeptions.UnknownFileFormatException;
import kernbeisser.Main;
import kernbeisser.Useful.Date;
import kernbeisser.Useful.Tools;
import lombok.Getter;

public class CatalogImporter {

  @Getter private String address;
  @Getter private String scope; // V - complete list, T - partial list, S - special list
  @Getter private String description;
  @Getter private Instant validFrom;
  @Getter private Instant validTo;
  @Getter private Instant createdDate;
  @Getter private Instant createdTime;
  @Getter private String infoLine;
  private int fileSeqNo;

  public static final String DELIMITER = ";";
  @Getter private final List<CatalogImportError> readErrors = new ArrayList<>();
  @Getter private final List<CatalogDataSource> catalog = new ArrayList<>();

  public String getScopeDescription() {
    switch (scope) {
      case "V":
        return "vollständige Liste";
      case "T":
        return "Teilliste";
      case "S":
        return "Sonderliste";
      default:
        return "unbekannt";
    }
  }

  private static Instant parseInstant(Field field, Instant date, String s)
      throws DateTimeParseException {
    if (field.getName().equals("aenderungsZeit")) {
      return Date.parseInstantTime(s, date, Date.INSTANT_CATALOG_TIME);
    }
    return Date.parseInstantDate(s, Date.INSTANT_CATALOG_DATE);
  }

  private static MetricUnits parseUnit(String s) throws InvalidValue {
    switch (s) {
      case "kg":
        return MetricUnits.KILOGRAM;
      case "l":
        return MetricUnits.LITER;
      case "":
        return null;
      default:
        throw new InvalidValue("falsch codierte Einheit");
    }
  }

  private static VAT parseVAT(String s) throws InvalidValue {
    switch (s) {
      case "1":
        return VAT.LOW;
      case "2":
        return VAT.HIGH;
      case "3":
        throw new InvalidValue("Pauschale MWSt Sätze werden zurzeit nicht unterstützt!");
      default:
        throw new InvalidValue("falsch codierte Mehrwertsteuer");
    }
  }

  private static Boolean parseBoolean(String s, Field f) throws InvalidValue {
    String trueValue = "true";
    String falseValue = "false";
    BoolValues boolValues = f.getAnnotation(BoolValues.class);
    if (boolValues != null) {
      trueValue = boolValues.trueValue();
      falseValue = boolValues.falseValue();
    }
    if (trueValue.equals(s)) return true;
    if (falseValue.equals(s)) return false;
    return null;
  }

  private static void parseField(CatalogDataSource out, Field declaredField, String part)
      throws NumberFormatException, IllegalAccessException, DateTimeParseException, InvalidValue {
    declaredField.setAccessible(true);
    Class<?> type = declaredField.getType();
    if (type.equals(String.class)) {
      declaredField.set(out, part);
      if (declaredField.getName().equals("aenderungskennung") && !"A;X;N;R;V;W".contains(part)) {
        throw new InvalidValue("ungültige Änderungskennung: " + part);
      }
    } else if (type.equals(VAT.class)) {
      declaredField.set(out, parseVAT(part));
    } else if (type.equals(Boolean.class))
      declaredField.set(out, parseBoolean(part, declaredField));
    else {
      if (!part.replace(" ", "").equals("")) {
        if (type.equals(Double.class))
          declaredField.set(
              out, tryParse(part.replace(",", ".").replace(" ", ""), Double::parseDouble));
        else if (type.equals(Integer.class))
          declaredField.set(out, tryParse(part.replace(" ", ""), Integer::parseInt));
        else if (type.equals(Long.class))
          declaredField.set(out, tryParse(part.replace(" ", ""), Long::parseLong));
        else if (type.equals(Instant.class))
          declaredField.set(
              out, tryParse(part, e -> parseInstant(declaredField, out.getAenderungsDatum(), e)));
        else if (type.equals(MetricUnits.class)) declaredField.set(out, parseUnit(part));
      }
    }
  }

  public static CatalogDataSource parseRowCore(
      String[] parts, BiConsumer<Exception, String[]> FormatExceptionHandler) {
    CatalogDataSource out = new CatalogDataSource();
    Field[] declaredFields = CatalogDataSource.class.getDeclaredFields();
    for (int i = 0; i < declaredFields.length; i++) {
      try {
        parseField(out, declaredFields[i], parts[i].trim());
      } catch (NumberFormatException
          | IllegalAccessException
          | DateTimeParseException
          | InvalidValue e) {
        FormatExceptionHandler.accept(e, new String[] {parts[i], declaredFields[i].getName()});
      } catch (ArrayIndexOutOfBoundsException e) {
        return out;
      }
    }
    return out;
  }

  public static CatalogDataSource parseRowWithLog(String[] parts, List<Exception> errorLog) {
    return parseRowCore(
        parts,
        (e, value_field) -> {
          errorLog.add(
              new Exception(
                  "Fehler beim Schreiben des Wertes \""
                      + value_field[0]
                      + "\" in das Feld "
                      + value_field[1],
                  e));
        });
  }

  public static CatalogDataSource parseRow(String[] parts) {
    return parseRowCore(
        parts,
        (e, value_field) -> {
          Main.logger.error(
              "Catalog error: cannot parse value \""
                  + value_field[0]
                  + "\" into field "
                  + value_field[1],
              e);
          Tools.showUnexpectedErrorWarning(e);
        });
  }

  private static <T> T tryParse(String in, Function<String, T> function)
      throws NumberFormatException, DateTimeParseException {
    return function.apply(in);
  }

  public void parseHeader(String[] parts) throws UnknownFileFormatException {
    DateTimeFormatter dateFormatter = Date.INSTANT_CATALOG_DATE;
    DateTimeFormatter timeFormatter = Date.INSTANT_CATALOG_TIME;
    String fileFormat = parts[0];
    String formatVersion = parts[1];
    if (!(fileFormat.equals("BNN") && formatVersion.equals("3"))) {
      throw new UnknownFileFormatException(
          "Unbekanntes Format oder Version: " + fileFormat + " V. " + formatVersion);
    }
    String encoding = parts[2];
    switch (encoding) {
      case "0":
        Charset charset = Charset.forName("CP850");
        break;
      case "1":
        charset = Charset.forName("CP1252");
        break;
      default:
        throw new UnknownFileFormatException("Unbenkannte Kodierung: " + encoding);
    }
    address = parts[3];
    scope = parts[4];
    description = parts[5];
    String currency = parts[6];
    if (!currency.equals("EUR")) {
      throw new UnknownFileFormatException("Falsche Währung: " + currency);
    }
    validFrom = Date.parseInstantDate(parts[7], dateFormatter);
    validTo = Date.parseInstantDate(parts[8], dateFormatter);
    createdDate = Date.parseInstantDate(parts[9], dateFormatter);
    createdTime = Date.parseInstantTime(parts[10], createdDate, timeFormatter);
    fileSeqNo = Integer.parseInt(parts[11]);
  }

  public CatalogImporter(Path bnnFile) {
    try {
      List<String> catalogSource = Files.readAllLines(bnnFile, Catalog.DEFAULT_ENCODING);
      infoLine = catalogSource.get(0);
      parseHeader(infoLine.split(DELIMITER));
      int followUpSeqNo =
          Integer.parseInt(catalogSource.get(catalogSource.size() - 1).split(DELIMITER)[2]);
      if (fileSeqNo != 1 || followUpSeqNo != 99) {
        throw new UnknownFileFormatException("Kann keine mehrteiligen Katalogdateien verarbeiten!");
      }
      for (int i = 1; i < catalogSource.size() - 1; i++) {
        List<Exception> rowLog = new ArrayList<>();
        catalog.add(parseRowWithLog(catalogSource.get(i).split(DELIMITER), rowLog));
        for (Exception e : rowLog) {
          readErrors.add(new CatalogImportError(i, e));
        }
      }
    } catch (Exception e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }
}
