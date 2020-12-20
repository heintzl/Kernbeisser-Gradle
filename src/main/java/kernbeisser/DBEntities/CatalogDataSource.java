package kernbeisser.DBEntities;

import java.lang.reflect.Field;
import java.util.function.Function;
import javax.persistence.*;
import kernbeisser.Useful.Tools;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Data
@Table
@Entity
@Setter(AccessLevel.NONE)
public class CatalogDataSource {
  private Integer artikelNr;
  private String aenderungskennung;
  private Integer aenderungsDatum;
  private Integer aenderungsZeit;
  private Long eANladen;
  private Long eANbestell;
  private String bezeichnung;
  private String bezeichnung2;
  private String bezeichnung3;
  private String handelsklasse;
  private String hersteller;
  private String hersteller2;
  private String herkunft;
  private String qualitaet;
  private String kontrollstelle;
  private Integer mHDRestlaufzeit;
  private Integer wGBNN;
  private Integer wGIfH;
  private Integer wGGH;
  private String ersatzArtikelNr;
  private Integer minBestellMenge;
  private String bestelleinheit;
  private Double bestelleinheitsMenge;
  private String ladeneinheit;
  private Integer mengenfaktor;
  private String gewichtsartikel;
  private String pfandNrLadeneinheit;
  private String pfandNrBestelleinheit;
  private Integer gewichtLadeneinheit;
  private String gewichtBestelleinheit;
  private Integer breite;
  private Integer hoehe;
  private Integer tiefe;
  private Integer mwstKennung;
  private Double vkFestpreis;
  private Double empfVk;
  private Double empfVkGH;
  private Double preis;
  private String rabattfaehig;
  private String skontierfaehig;
  private Double staffelMenge1;
  private Double staffelPreis1;
  private String rabattfaehig1;
  private String skontierfaehig1;
  private Double staffelMenge2;
  private Double staffelPreis2;
  private String rabattfaehig2;
  private String skontierfaehig2;
  private Double staffelMenge3;
  private Double staffelPreis3;
  private String rabattfaehig3;
  private String skontierfaehig3;
  private Double staffelMenge4;
  private Double staffelPreis4;
  private String rabattfaehig4;
  private String skontierfaehig4;
  private Double staffelMenge5;
  private Double staffelPreis5;
  private String rabattfaehig5;
  private String skontierfaehig5;
  private String artikelart;
  private String aktionspreis;
  private Integer aktionspreisGueltigAb;
  private Integer aktionspreisGueltigBis;
  private Double empfVkAktion;
  private String grundpreisEinheit;
  private Double grundpreisFaktor;
  private Integer lieferbarAb;
  private Integer lieferbarBis;

  @Id
  @GeneratedValue
  @GenericGenerator(name = "increment", strategy = "increment")
  private long id;

  public CatalogDataSource() {}

  public static CatalogDataSource parseRow(String[] parts) {
    CatalogDataSource out = new CatalogDataSource();
    Field[] declaredFields = CatalogDataSource.class.getDeclaredFields();
    for (int i = 0; i < declaredFields.length; i++) {
      try {
        Field declaredField = declaredFields[i];
        declaredField.setAccessible(true);
        Class<?> type = declaredField.getType();
        if (type.equals(String.class)) declaredField.set(out, parts[i]);
        else {
          if (!parts[i].replace(" ", "").equals("")) {
            if (type.equals(Double.class))
              declaredField.set(
                  out, tryParse(parts[i].replace(",", ".".replace(" ", "")), Double::parseDouble));
            else if (type.equals(Integer.class))
              declaredField.set(out, tryParse(parts[i].replace(" ", ""), Integer::parseInt));
            else if (type.equals(Long.class))
              declaredField.set(out, tryParse(parts[i].replace(" ", ""), Long::parseLong));
          }
        }
      } catch (NumberFormatException | IllegalAccessException e) {
        System.err.println(i);
        System.err.println(declaredFields[i]);
        Tools.showUnexpectedErrorWarning(e);
      } catch (ArrayIndexOutOfBoundsException e) {
        return out;
      }
    }
    return out;
  }

  private static <T> T tryParse(String in, Function<String, T> function)
      throws NumberFormatException {
    return function.apply(in);
  }
}
