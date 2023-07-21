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

  private Charset charset;
  @Getter private String address;
  private String scope; // V - complete list, T - partial list, S - special list
  @Getter private String description;
  private String currency;
  @Getter private Instant validFrom;
  @Getter private Instant validTo;
  @Getter private Instant createdDate;
  @Getter private Instant createdTime;
  private int fileSeqNo;
  private int followUpSeqNo;

  private static final String DELIMITER = ";";
  @Getter private final List<CatalogImportError> readErrors = new ArrayList<>();
  @Getter private final List<CatalogDataSource> catalog = new ArrayList<>();

  public String getScope() {
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

  private static void parseField(CatalogDataSource out, Field declaredField, String part)
      throws NumberFormatException, IllegalAccessException, DateTimeParseException, InvalidValue {
    declaredField.setAccessible(true);
    Class<?> type = declaredField.getType();
    if (type.equals(String.class)) {
      declaredField.set(out, part);
      if (declaredField.getName().equals("aenderungskennung") && !"A;X;N;R;V;W".contains(part)) {
        throw new InvalidValue("ungültige Änderungskennung: " + part);
      }
    } else if (type.equals(VAT.class)) declaredField.set(out, parseVAT(part));
    else {
      if (!part.replace(" ", "").equals("")) {
        if (type.equals(Double.class))
          declaredField.set(
              out, tryParse(part.replace(",", ".").replace(" ", ""), Double::parseDouble));
        else if (type.equals(Integer.class))
          declaredField.set(out, tryParse(part.replace(" ", ""), Integer::parseInt));
        else if (type.equals(Long.class))
          declaredField.set(out, tryParse(part.replace(" ", ""), Long::parseLong));
        else if (type.equals(Boolean.class)) declaredField.set(out, part.equals("J"));
        else if (type.equals(Instant.class))
          declaredField.set(
              out, tryParse(part, e -> parseInstant(declaredField, out.getAenderungsDatum(), e)));
        else if (type.equals(MetricUnits.class)) declaredField.set(out, parseUnit(part));
      }
    }
  }

  public static CatalogDataSource parseRowWithLog(String[] parts, List<Exception> errorLog) {
    CatalogDataSource out = new CatalogDataSource();
    Field[] declaredFields = CatalogDataSource.class.getDeclaredFields();
    for (int i = 0; i < declaredFields.length; i++) {
      try {
        parseField(out, declaredFields[i], parts[i]);
      } catch (NumberFormatException
          | IllegalAccessException
          | DateTimeParseException
          | InvalidValue e) {
        errorLog.add(
            new Exception(
                "Fehler beim Schreiben des Wertes \""
                    + parts[i]
                    + "\" in das Feld "
                    + declaredFields[i].getName(),
                e));
      } catch (ArrayIndexOutOfBoundsException e) {
        return out;
      }
    }
    return out;
  }

  public static CatalogDataSource parseRow(String[] parts) {
    CatalogDataSource out = new CatalogDataSource();
    Field[] declaredFields = CatalogDataSource.class.getDeclaredFields();
    for (int i = 0; i < declaredFields.length; i++) {
      try {
        parseField(out, declaredFields[i], parts[i]);
      } catch (NumberFormatException
          | IllegalAccessException
          | DateTimeParseException
          | InvalidValue e) {
        Main.logger.error(
            "Catalog error: cannot parse value \""
                + parts[i]
                + "\" into field "
                + declaredFields[i].getName(),
            e);
        Tools.showUnexpectedErrorWarning(e);
      } catch (ArrayIndexOutOfBoundsException e) {
        return out;
      }
    }
    return out;
  }

  private static <T> T tryParse(String in, Function<String, T> function)
      throws NumberFormatException, DateTimeParseException {
    return function.apply(in);
  }

  private CatalogDataSource readCatalogEntry(String line, List<Exception> rowLog) {
    CatalogDataSource out = parseRowWithLog(line.split(DELIMITER), rowLog);
    return out;
  }

  private void parseHeader(String[] parts) throws UnknownFileFormatException {
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
        charset = Charset.forName("CP850");
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
    currency = parts[6];
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
      String[] headerParts = catalogSource.get(0).split(DELIMITER);
      parseHeader(headerParts);
      followUpSeqNo =
          Integer.parseInt(catalogSource.get(catalogSource.size() - 1).split(DELIMITER)[2]);
      if (fileSeqNo != 1 || followUpSeqNo != 99) {
        throw new UnknownFileFormatException("Kann keine mehrteiligen Katalogdateien verarbeiten!");
      }
      for (int i = 1; i < catalogSource.size() - 1; i++) {
        List<Exception> rowLog = new ArrayList<>();
        catalog.add(readCatalogEntry(catalogSource.get(i), rowLog));
        for (Exception e : rowLog) {
          readErrors.add(new CatalogImportError(i, e));
        }
      }
    } catch (Exception e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }
}
