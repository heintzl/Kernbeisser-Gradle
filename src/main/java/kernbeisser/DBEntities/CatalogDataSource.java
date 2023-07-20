package kernbeisser.DBEntities;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Function;
import javax.persistence.*;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.VAT;
import kernbeisser.Exeptions.InvalidValue;
import kernbeisser.Main;
import kernbeisser.Useful.Date;
import kernbeisser.Useful.Tools;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.hibernate.annotations.GenericGenerator;

@Data
@Table
@Entity
@Setter(AccessLevel.NONE)
@Getter(AccessLevel.PUBLIC)
public class CatalogDataSource {
  @Id
  @GeneratedValue
  @GenericGenerator(name = "increment", strategy = "increment")
  private String artikelNr;

  // CatalogChange.identifier
  @Required private String aenderungskennung;
  @Required private Instant aenderungsDatum;
  private Instant aenderungsZeit;
  private Long eanLadenEinheit;
  private Long eanBestellEinheit;
  @Required private String bezeichnung;
  private String bezeichnung2;
  private String bezeichnung3;

  // roman Number
  private String handelsklasse;

  // BNN-Markenkürzel
  @Required private String marke;

  // deprecated Herstellerkürzel
  private String hersteller;

  // car plate country code
  @Required private String herkunft;

  // according to BNN-IK list
  @Required private String qualitaet;
  private String kontrollstelle;

  // days
  private Integer mHDRestlaufzeit;

  // wg* trade group properties
  private Integer wgBnn;
  private Integer wgIfh;
  private Integer wgGh;

  // is delivered if article is not on stock
  private String ersatzArtikelNr;
  private Double minBestellMenge;
  @Required private String bestelleinheit;
  @Required private Double bestelleinheitsMenge;
  @Required private String ladeneinheit;
  @Required private Double mengenfaktor;
  private Boolean gewichtsartikel;
  @Required private String pfandNrLadeneinheit;
  private String pfandNrBestelleinheit;
  private Double gewichtLadeneinheit;
  @Required private Double gewichtBestelleinheit;

  // size in cm
  private Integer breite;
  private Integer hoehe;
  private Integer tiefe;
  @Required private VAT mwstKennung;
  private Double vkFestpreis;
  private Double empfVk;
  private Double empfVkGH;
  @Required private Double preis;
  private Boolean rabattfaehig;
  private Boolean skontierfaehig;
  private Double staffelMenge1;
  private Double staffelPreis1;
  private Boolean rabattfaehig1;
  private Boolean skontierfaehig1;
  private Double staffelMenge2;
  private Double staffelPreis2;
  private Boolean rabattfaehig2;
  private Boolean skontierfaehig2;
  private Double staffelMenge3;
  private Double staffelPreis3;
  private Boolean rabattfaehig3;
  private Boolean skontierfaehig3;
  private Double staffelMenge4;
  private Double staffelPreis4;
  private Boolean rabattfaehig4;
  private Boolean skontierfaehig4;
  private Double staffelMenge5;
  private Double staffelPreis5;
  private Boolean rabattfaehig5;
  private Boolean skontierfaehig5;
  private String artikelart;
  private Boolean aktionspreis;
  private Instant aktionspreisGueltigAb;
  private Instant aktionspreisGueltigBis;
  private Double empfVkAktion;

  // should be @Required but isn't in bnn
  private MetricUnits grundpreisEinheit;
  @Required private Double grundpreisFaktor;
  private Instant lieferbarAb;
  private Instant lieferbarBis;
  private String artikelBioId;
  private Integer artikelVariant;
  private String markenId;
  private String herstellerId;
  private long id;

  public CatalogDataSource() {}

  private static Instant parseInstant(Field field, String s) throws DateTimeParseException {
    if (field.getName().equals("aenderungsZeit")) {
      return Date.parseInstantTime(s, Date.INSTANT_CATALOG_TIME);
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
    if (type.equals(String.class)) declaredField.set(out, part);
    else if (type.equals(VAT.class)) declaredField.set(out, parseVAT(part));
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
          declaredField.set(out, tryParse(part, e -> parseInstant(declaredField, e)));
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
                "Catalog error: cannot parse value \""
                    + parts[i]
                    + "\" into field "
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

  public int getArtikelNr() throws NumberFormatException {
    return Integer.parseInt(artikelNr);
  }

  private static <T> T tryParse(String in, Function<String, T> function)
      throws NumberFormatException, DateTimeParseException {
    return function.apply(in);
  }
}
